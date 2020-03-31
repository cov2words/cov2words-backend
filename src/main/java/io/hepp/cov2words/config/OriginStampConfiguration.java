package io.hepp.cov2words.config;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Contains the configuration of the OriginStamp client.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Configuration
@Getter
public class OriginStampConfiguration {

    /**
     * Endpoint for accessing the OriginStamp API.
     */
    @Value("${originstamp.endpoint.base}")
    @NotEmpty
    private String endpoint;

    /**
     * API key for the authentication with OriginStamp.
     */
    @NotEmpty
    @Length(max = 36, min = 36)
    @Value("${originstamp.api_key}")
    private String apiKey;

    /**
     * User-Agent value that is used.
     */
    @Value("${originstamp.user_agent}")
    private String userAgent;

    /**
     * OriginStamp endpoint for creating a new timestamp.
     */
    @NotEmpty
    @Value("${originstamp.endpoint.timestamp.create}")
    private String createEndpoint;

    /**
     * OriginStamp endpoint to fetch the timestamp status for a certain hash.
     */
    @Value("${originstamp.endpoint.timestamp.status}")
    @NotEmpty
    private String statusEndpoint;

    /**
     * OriginStamp endpoint for downloading the certificates.
     */
    @Value("${originstamp.endpoint.proof.url}")
    @NotEmpty
    private String proofEndpoint;

    /**
     * Connection timeout.
     */
    @Value("${originstamp.client.timeout:1000}")
    @NotEmpty
    private int downloadClientTimeout;

    /**
     * Timeout for client read.
     */
    @Value("${originstamp.client.read_timeout:1000}")
    private int downloadClientReadTimeout;

    @Value("${originstamp.currencies:1}")
    private List<Integer> currencies;
}
