package io.hepp.cov2words.service.originstamp;

import io.hepp.cov2words.common.constant.originstamp.TimestampStatus;
import io.hepp.cov2words.common.dto.originstamp.ProofResponseDTO;
import io.hepp.cov2words.common.dto.originstamp.TimestampResponseDTO;
import io.hepp.cov2words.common.exceptions.answer.AnswerHashNotExistException;
import io.hepp.cov2words.common.exceptions.answer.HashNotTimestampedException;
import io.hepp.cov2words.common.exceptions.answer.InvalidHashException;
import io.hepp.cov2words.common.exceptions.originstamp.OriginStampApiException;
import io.hepp.cov2words.common.util.HashUtil;
import io.hepp.cov2words.common.util.OriginStampUtil;
import io.hepp.cov2words.domain.entity.AnswerEntity;
import io.hepp.cov2words.domain.entity.AnswerTimestampMapping;
import io.hepp.cov2words.domain.entity.TimestampEntity;
import io.hepp.cov2words.domain.repository.AnswerTimestampRepository;
import io.hepp.cov2words.domain.repository.TimestampRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Service that orchestrates the generation of timestamps.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Service
@Slf4j(topic = "OriginStampService")
public class OriginStampService {

    private final OriginStampClient client;
    private final TimestampRepository timestampRepository;
    private final AnswerTimestampRepository answerTimestampRepository;
    private final int pageSize;
    private final HashSet<Integer> currencies;

    @Autowired
    public OriginStampService(
            OriginStampClient client,
            TimestampRepository timestampRepository,
            AnswerTimestampRepository answerTimestampRepository,
            @Value("${cov2words.database.batch.size}") int pageSize
    ) {
        this.client = client;
        this.timestampRepository = timestampRepository;
        this.answerTimestampRepository = answerTimestampRepository;
        this.pageSize = pageSize;
        this.currencies = new HashSet<>(this.client.getConfiguration().getCurrencies());
    }

    public Optional<AnswerTimestampMapping> getTimestampForAnswer(AnswerEntity answer) {
        return this.answerTimestampRepository
                .findFirstByDateInvalidatedIsNullAndAnswerEntity_Id(answer.getId());
    }

    /**
     * Returns the proof for a certain hash.
     */
    public byte[] getProofForHash(String hash) throws
            InvalidHashException,
            AnswerHashNotExistException,
            HashNotTimestampedException {
        log.info("Fetching certificate for hash");
        if (StringUtils.isEmpty(hash)) {
            throw new InvalidHashException();
        }

        if (!HashUtil.isValidSHA256(hash)) {
            throw new InvalidHashException();
        }

        TimestampEntity timestamp = this.timestampRepository.findFirstByHashAndDateInvalidatedIsNull(hash)
                .orElseThrow(AnswerHashNotExistException::new);

        // Checking the status
        if (timestamp.getStatus() < TimestampStatus.CERTIFIED.getStatusId()) {
            throw new HashNotTimestampedException();
        }

        return timestamp.getCertificate();
    }

    public AnswerTimestampMapping createTimestamp(AnswerEntity answer) throws NoSuchAlgorithmException {
        // Calculating the hash.
        String sha256 = HashUtil.getSHA256(answer.getAnswer().getBytes());

        // Checking if hash already exists.
        Optional<TimestampEntity> timestampOptional = this.timestampRepository.findFirstByHashAndDateInvalidatedIsNull(
                sha256
        );

        DateTime now = DateTime.now();

        return timestampOptional.map(timestampEntity -> this.answerTimestampRepository.save(
                new AnswerTimestampMapping(
                        UUID.randomUUID(),
                        now,
                        now,
                        null,
                        timestampEntity,
                        answer
                ))).orElseGet(() -> this.answerTimestampRepository.save(
                new AnswerTimestampMapping(
                        UUID.randomUUID(),
                        now,
                        now,
                        null,
                        new TimestampEntity(
                                UUID.randomUUID(),
                                sha256,
                                null,
                                null,
                                null,
                                this.client.getConfiguration().getCurrencies().get(0),
                                TimestampStatus.UNSUBMITTED.getStatusId(),
                                now,
                                now,
                                null,
                                null
                        ),
                        answer
                )));
    }

    /**
     * Scheduled job that is used to create new timestamps.
     */
    @Scheduled(cron = "${originstamp.cron.create_timestamps}")
    public void submitTimestamps() {
        log.info("Submitting timestamps to OriginStamp");
        Pageable pageRequest = PageRequest.of(0, this.pageSize);
        Page<TimestampEntity> page;
        DateTime maximumAge = DateTime.now().minusMinutes(5);

        do {
            page = this.timestampRepository.findAllByDateInvalidatedIsNullAndStatusLessThanAndDateModifiedLessThan(
                    TimestampStatus.PROCESSING.getStatusId(),
                    maximumAge,
                    pageRequest
            );

            List<CompletableFuture<TimestampEntity>> futures = page.stream().map(file -> {
                try {
                    return this.createTimestamps(file);
                } catch (IOException | OriginStampApiException e) {
                    log.error("Could not connect with OriginStamp", e);
                }
                return CompletableFuture.completedFuture(file);
            }).collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

            this.timestampRepository.saveAll(
                    futures.stream()
                            .map(future -> {
                                try {
                                    return future.get();
                                } catch (InterruptedException | ExecutionException e) {
                                    log.error("An unexpected error occurred during asynchronous execution", e);
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );

            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }

    /**
     * Scheduled job that is used to check the status.
     */
    @Scheduled(cron = "${originstamp.cron.update_timestamps}")
    public void updateTimestamps() {
        log.info("Updating timestamps from OriginStamp");
        Pageable pageRequest = PageRequest.of(0, this.pageSize);
        Page<TimestampEntity> page;
        DateTime maximumAge = DateTime.now().minusMinutes(5);

        do {
            page = this.timestampRepository.findAllByDateInvalidatedIsNullAndStatusLessThanAndDateModifiedLessThan(
                    TimestampStatus.FINISHED.getStatusId(),
                    maximumAge,
                    pageRequest
            );

            List<CompletableFuture<TimestampEntity>> futures = page.stream().map(file -> {
                try {
                    return this.updateTimestamps(file);
                } catch (IOException | OriginStampApiException e) {
                    log.error("Could not connect with OriginStamp", e);
                }
                return CompletableFuture.completedFuture(file);
            }).collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

            this.timestampRepository.saveAll(
                    futures.stream()
                            .map(future -> {
                                try {
                                    return future.get();
                                } catch (InterruptedException | ExecutionException e) {
                                    log.error("An unexpected error occurred during asynchronous execution", e);
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );

            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }

    /**
     * Scheduled job that fetches the certificates from OriginStamp.
     */
    @Scheduled(cron = "${originstamp.cron.download_certificates}")
    public void fetchCertificates() {
        log.info("Submitting timestamps to OriginStamp");
        Pageable pageRequest = PageRequest.of(0, this.pageSize);
        Page<TimestampEntity> page;
        DateTime maximumAge = DateTime.now().minusMinutes(5);

        do {
            page = this.timestampRepository.findAllByDateInvalidatedIsNullAndStatusLessThanAndDateModifiedLessThan(
                    TimestampStatus.PROCESSING.getStatusId(),
                    maximumAge,
                    pageRequest
            );

            List<CompletableFuture<TimestampEntity>> futures = page.stream()
                    .map(this::fetchCertificate)
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

            this.timestampRepository.saveAll(
                    futures.stream()
                            .map(future -> {
                                try {
                                    return future.get();
                                } catch (InterruptedException | ExecutionException e) {
                                    log.error("An unexpected error occurred during asynchronous execution", e);
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );

            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }

    @Async
    public CompletableFuture<TimestampEntity> fetchCertificate(TimestampEntity timestamp) {
        log.info("Creating timestamps for file: {}", timestamp);
        try {
            ProofResponseDTO response = this.client.getProof(
                    timestamp.getHash(),
                    timestamp.getCurrency()
            );

            byte[] certificate = OriginStampUtil.downloadFile(
                    new URL(response.getDownloadURL()),
                    this.client.getConfiguration()
            );

            timestamp.setCertificate(certificate);
            timestamp.setDateModified(DateTime.now().plusMinutes(30));
            timestamp.setStatus(TimestampStatus.CERTIFIED.getStatusId());
        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
        }

        return CompletableFuture.completedFuture(timestamp);
    }

    @Async
    public CompletableFuture<TimestampEntity> updateTimestamps(
            TimestampEntity timestamp
    ) throws IOException, OriginStampApiException {
        log.info("Updating timestamps for file: {}", timestamp);
        TimestampResponseDTO response = this.client.getTimestamp(timestamp.getHash());
        return this.processResponse(response, this.currencies, timestamp);
    }

    @Async
    public CompletableFuture<TimestampEntity> createTimestamps(
            TimestampEntity timestamp
    ) throws IOException, OriginStampApiException {
        log.info("Creating timestamps for file: {}", timestamp);
        TimestampResponseDTO response = this.client.createTimestamp(timestamp.getHash());
        return this.processResponse(response, this.currencies, timestamp);
    }

    private CompletableFuture<TimestampEntity> processResponse(
            TimestampResponseDTO response,
            HashSet<Integer> currencies,
            TimestampEntity timestamp
    ) {
        timestamp = OriginStampUtil.setTimestamp(
                response,
                currencies,
                timestamp
        );

        return CompletableFuture.completedFuture(timestamp);
    }
}
