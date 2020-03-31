package io.hepp.cov2words.service.originstamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hepp.cov2words.common.constant.originstamp.ProofType;
import io.hepp.cov2words.common.dto.originstamp.*;
import io.hepp.cov2words.common.exceptions.originstamp.OriginStampApiException;
import io.hepp.cov2words.config.OriginStampClientInterceptor;
import io.hepp.cov2words.config.OriginStampConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Service that is used to access the OriginStamp API.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Service
@Slf4j(topic = "OriginStampClient")
public class OriginStampClient {
    @Getter
    private final OriginStampConfiguration configuration;

    @Autowired
    public OriginStampClient(OriginStampConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Async method that is used to create the timestamp.
     */
    public TimestampResponseDTO createTimestamp(String hash) throws IOException, OriginStampApiException {
        log.info("Submitting hash: {} to OriginStamp", hash);
        // Preparing the request payload.
        TimestampRequestDTO request = new TimestampRequestDTO(
                null,
                null,
                hash,
                new ArrayList<>()
        );

        ServiceResponseDTO serviceResponseDTO = this.performRequest(
                this.configuration.getEndpoint() + this.configuration.getCreateEndpoint(),
                request,
                HttpMethod.POST
        );

        return new ObjectMapper().readValue(serviceResponseDTO.getResponseBody().toString(), TimestampResponseDTO.class);
    }

    private ServiceResponseDTO performRequest(
            String endpoint,
            OriginStampRequest request,
            HttpMethod httpMethod
    ) throws IOException, OriginStampApiException {
        log.info("Performing request to {}", endpoint);
        RestTemplate restTemplate = this.getRestTemplate();

        ResponseEntity<ServiceResponseDTO> responseEntity = restTemplate.exchange(
                endpoint,
                httpMethod,
                (request == null) ? HttpEntity.EMPTY : new HttpEntity<>(request),
                new ParameterizedTypeReference<ServiceResponseDTO>() {
                }
        );

        if (!responseEntity.hasBody() || responseEntity.getStatusCode() != HttpStatus.OK) {
            log.warn("Response has no body: HTTP Status Code {}", responseEntity.getStatusCodeValue());
            throw new IOException("Connection to microservice possibly not available.");
        }

        ServiceResponseDTO responseDTO = responseEntity.getBody();

        assert responseDTO != null;
        if (responseDTO.getErrorCode() != 0) {
            log.warn("Response contains an error with message: {}\terror code: {}",
                    responseDTO.getErrorMessage(), responseDTO.getErrorCode());
            throw new OriginStampApiException(responseDTO.getErrorMessage());
        }

        log.debug("Returning response: {}", responseDTO);
        return responseDTO;
    }

    /**
     * Async method that is used to create the timestamp.
     */
    public TimestampResponseDTO getTimestamp(String hash) throws IOException, OriginStampApiException {
        log.info("Checking status of hash: {}", hash);
        ServiceResponseDTO response = this.performRequest(
                this.configuration.getEndpoint() +
                        String.format(this.configuration.getStatusEndpoint(), hash),
                null,
                HttpMethod.GET
        );

        return new ObjectMapper().readValue(response.getResponseBody().toPrettyString(), TimestampResponseDTO.class);
    }

    /**
     * Returns the PDF certificate for a certain hash and currency.
     */
    public ProofResponseDTO getProof(String hash, int currency) throws IOException, OriginStampApiException {
        log.info("Requesting proof for hash: {} and currency: {}", hash, currency);
        ServiceResponseDTO response = this.performRequest(
                this.configuration.getEndpoint() + this.configuration.getProofEndpoint(),
                new ProofRequestDTO(
                        currency,
                        hash,
                        ProofType.CERTIFICATE.getStatusId()
                ),
                HttpMethod.POST
        );

        return new ObjectMapper().readValue(response.getResponseBody().toString(), ProofResponseDTO.class);
    }

    /**
     * Provides a RESTful client that can be used for accessing external APIs.
     *
     * @return Prepared REST template with authentication interceptor.
     */
    private RestTemplate getRestTemplate() {
        log.trace("Creating RestTemplate for request");
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.setInterceptors(Collections.singletonList(
                new OriginStampClientInterceptor(
                        this.configuration.getApiKey(),
                        this.configuration.getUserAgent()
                )));
        return restTemplate;
    }
}
