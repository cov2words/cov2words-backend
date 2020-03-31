package io.hepp.cov2words.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * The interceptor is used for calling external APIs. The corresponding headers are set if configured.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@SuppressWarnings("SpellCheckingInspection")
public class OriginStampClientInterceptor implements ClientHttpRequestInterceptor {
    private final Optional<String> apiKey;
    private final Optional<String> userAgent;

    public OriginStampClientInterceptor(
            String apiKey,
            String userAgent
    ) {
        this.apiKey = Optional.ofNullable(apiKey);
        this.userAgent = Optional.ofNullable(userAgent);
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        HttpHeaders headers = request.getHeaders();

        this.apiKey.ifPresent(key -> headers.add(
                HttpHeaders.AUTHORIZATION,
                key
        ));

        this.userAgent.ifPresent(key -> headers.add(
                HttpHeaders.USER_AGENT,
                key
        ));
        return execution.execute(request, body);
    }
}
