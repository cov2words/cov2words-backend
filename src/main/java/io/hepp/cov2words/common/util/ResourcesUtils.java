package io.hepp.cov2words.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Utils that are used to access files from resources files.
 * Source: https://stackoverflow.com/a/44399541
 */
public class ResourcesUtils {
    public static String getResourceFileAsString(String fileName) {
        InputStream is = getResourceFileAsInputStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return (String) reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } else {
            return null;
        }
    }

    public static InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = ResourcesUtils.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }
}
