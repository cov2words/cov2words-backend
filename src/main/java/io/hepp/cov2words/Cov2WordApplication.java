package io.hepp.cov2words;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Class that starts the backend.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Cov2WordApplication {
    public static void main(String[] arguments) {
        SpringApplication.run(Cov2WordApplication.class, arguments);
    }
}
