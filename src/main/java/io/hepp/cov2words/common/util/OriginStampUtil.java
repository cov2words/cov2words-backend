package io.hepp.cov2words.common.util;

import io.hepp.cov2words.common.constant.originstamp.OriginStampCurrency;
import io.hepp.cov2words.common.constant.originstamp.TimestampStatus;
import io.hepp.cov2words.common.dto.originstamp.TimestampResponseDTO;
import io.hepp.cov2words.config.OriginStampConfiguration;
import io.hepp.cov2words.domain.entity.TimestampEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@Slf4j(topic = "OriginStampUtil")
public class OriginStampUtil {

    /**
     * Estimates the next event date and sets it to the document to decrease load.
     */
    public static DateTime estimateNextEvent(int currencyId, int currentStatus) {
        DateTime estimation = DateTime.now();

        TimestampStatus statusType = TimestampStatus.forStatusId(currencyId);
        OriginStampCurrency currency = OriginStampCurrency.getCurrencyForId(currencyId);

        switch (currency) {
            case AION:
                switch (statusType) {
                    case UNSUBMITTED:
                        return estimation;
                    case PROCESSING:
                        return estimation;
                    case SUBMITTED:
                        return estimation.plusMinutes(3);
                    case TIMESTAMPED:
                        return estimation.plusMinutes(5);
                    case FINISHED:
                        return estimation.plusHours(1);
                    case CERTIFIED:
                    default:
                        return estimation.plusHours(1);
                }
            case ETHEREUM:
                switch (statusType) {
                    case UNSUBMITTED:
                        return estimation;
                    case PROCESSING:
                        return estimation.plusMinutes(20);
                    case SUBMITTED:
                        return estimation.plusMinutes(10);
                    case TIMESTAMPED:
                        return estimation.plusMinutes(15);
                    case FINISHED:
                        return estimation.plusHours(1);
                    case CERTIFIED:
                    default:
                        return estimation.plusHours(1);
                }
            case BITCOIN:
                switch (statusType) {
                    case UNSUBMITTED:
                        return estimation;
                    case PROCESSING:
                        return estimation.plusHours(1);
                    case SUBMITTED:
                        return estimation.plusMinutes(5);
                    case TIMESTAMPED:
                        return estimation.plusMinutes(5);
                    case FINISHED:
                        return estimation.plusHours(1);
                    case CERTIFIED:
                    default:
                        return estimation.plusHours(1);
                }
            case SUEDKURIER:
                switch (statusType) {
                    case UNSUBMITTED:
                        return estimation;
                    case PROCESSING:
                        return estimation.plusHours(12);
                    case SUBMITTED:
                        return estimation.plusHours(12);
                    case TIMESTAMPED:
                        return estimation.plusMinutes(1);
                    case FINISHED:
                        return estimation.plusMinutes(1);
                    case CERTIFIED:
                    default:
                        return estimation.plusHours(1);
                }
            default:
            case UNKNOWN:
                return estimation;
        }
    }

    /**
     * Sets the timestamp information to database entity.
     */
    public static TimestampEntity setTimestamp(
            TimestampResponseDTO response,
            HashSet<Integer> targetCurrencies,
            TimestampEntity timestampEntity
    ) {
        log.debug("Converting response to database entity");
        response.getTimestampDataDTOList()
                .stream()
                .filter(timestamp -> targetCurrencies.contains(timestamp.getCurrency()))
                .forEach(timestamp -> {
                    timestampEntity.setDateModified(
                            OriginStampUtil.estimateNextEvent(
                                    timestamp.getCurrency(),
                                    (int) timestamp.getStatus()
                            )
                    );
                    timestampEntity.setCurrency(timestamp.getCurrency());
                    timestampEntity.setRootHash(timestamp.getPrivateKey());
                    timestampEntity.setTransaction(timestamp.getTransaction());
                    timestampEntity.setBlockchainTimestamp(
                            timestamp.getTimestamp() != null ? new DateTime(timestamp.getTimestamp()) : null
                    );
                    timestampEntity.setStatus(timestamp.getStatus());
                });

        return timestampEntity;
    }

    public static byte[] downloadFile(
            URL url,
            OriginStampConfiguration configuration
    ) {
        try {
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", configuration.getUserAgent());

            connection.setConnectTimeout(configuration.getDownloadClientTimeout());
            connection.setReadTimeout(configuration.getDownloadClientReadTimeout());
            connection.connect();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            IOUtils.copy(connection.getInputStream(), outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            // Log error and return null, some default or throw a runtime exception
            log.error("An error occurred during the download", e);
            return null;
        }
    }
}
