package io.hepp.cov2words.config;

import com.google.common.base.Predicate;
import io.hepp.cov2words.common.constant.Paths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Configuration of the SwaggerHub Documentation.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    /**
     * Returns Docket for Swagger.
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(getDocumentedApiPaths())
                .build()
                .useDefaultResponseMessages(false)
                // Register security context to display authorization icon to include header parameter.
                .securitySchemes(defineApiKeys())
                .securityContexts(createSecurityContexts());
    }

    /**
     * Every REST Service we want to document with Swagger.
     *
     * @return Predicate conditions for api paths
     */
    @SuppressWarnings("Guava")
    private Predicate<String> getDocumentedApiPaths() {
        // Show all API paths.
        return or(
                regex(".*" + Paths.BASE_PATH + "/.*")
        );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Cov2words API Documentation")
                .description("This is the technical interface for the generation of word combinations")
                .version("3.0")
                .contact(new Contact("Cov2Words", "https://devpost.com/software/cov2words", "thomas@hepp.io"))
                .build();
    }

    private List<ApiKey> defineApiKeys() {
        return Collections.singletonList(
                new ApiKey("API Key Authorization", "Authorization", "header")
        );
    }

    private List<SecurityContext> createSecurityContexts() {
        return Collections.singletonList(
                // Display authorization icon for all API paths: /v3 and /api
                SecurityContext.builder()
                        .securityReferences(defaultAuth("API Key Authorization"))
                        .forPaths(
                                or(regex(Paths.BASE_PATH + "/.*"))
                        )
                        .build()
        );
    }

    private List<SecurityReference> defaultAuth(String apiKeyName) {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "access system key endpoints");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference(apiKeyName, authorizationScopes));
    }

    /**
     * Initiates security context.
     */
    @Bean
    public SecurityConfiguration security() {
        // Dummy configuration for swagger, needed to activate the authorization icon.
        return SecurityConfigurationBuilder.builder()
                .clientId("cov2words-test-id")
                .clientSecret("cov2words-test-secret")
                .realm("cov2words-test-realm")
                .appName("cov2words-test")
                .scopeSeparator(",")
                .additionalQueryStringParams(null)
                .useBasicAuthenticationWithAccessCodeGrant(false)
                .build();
    }
}
