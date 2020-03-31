package io.hepp.cov2words;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

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

    @Bean
    public Executor taskExecutor(
            @Value("${cov2words.database.batch.size:10}") int pageSize
    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(pageSize);
        executor.setMaxPoolSize(pageSize);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("Timestamp-");
        executor.initialize();
        return executor;
    }
}
