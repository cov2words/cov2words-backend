package io.hepp.cov2words.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Utils that are used to access files from resources files.
 * Source: https://stackoverflow.com/a/44399541
 */
@Slf4j(topic = "ResourcesUtil")
public class ResourcesUtil {
    public static String getResourceFileAsString(String fileName) {
        log.info("Loading file from resources: {}", fileName);
        InputStream is = null;
        try {
            is = getResourceFileAsInputStream(fileName);
        } catch (IOException e) {
            log.error("An unexpected error occurred", e);
        }

        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return (String) reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } else {
            return null;
        }
    }

    public static InputStream getResourceFileAsInputStream(String fileName) throws IOException {
        return getResourceFile(fileName).getInputStream();
    }

    public static Resource getResourceFile(String file) {
        return new ClassPathResource(file);
    }
}
