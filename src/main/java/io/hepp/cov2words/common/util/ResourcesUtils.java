package io.hepp.cov2words.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.stream.Collectors;

/**
 * Utils that are used to access files from resources files.
 * Source: https://stackoverflow.com/a/44399541
 */
@Slf4j(topic = "ResourcesUtils")
public class ResourcesUtils {
    public static String getResourceFileAsString(String fileName) {
        log.info("Loading file from resources: {}", fileName);
        InputStream is = null;
        try {
            is = getResourceFileAsInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return (String) reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } else {
            return null;
        }
    }

    public static InputStream getResourceFileAsInputStream(String fileName) throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:" + fileName);
        log.info("File exists: {}", file.exists());
        return new FileInputStream(file);
    }
}
