package io.hepp.cov2words.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Util that provides methods for handling hashes.
 */
public final class HashUtil {
    /**
     * Validates an input to be a SHA-1.
     * regex alphanumerical with length = 40
     */
    public static boolean isValidSHA1(String s) {
        return s.matches("[a-fA-F0-9]{40}");
    }

    /**
     * Validates an input to be a MD5 hash.
     * regex alphanumerical with length = 32
     */
    public static boolean isValidMD5(String s) {
        return s.matches("[a-fA-F0-9]{32}");
    }

    /**
     * Validates if a given input hash is a valid SHA-256.
     * regex alphanumerical with length = 64
     *
     * @param hash hash
     * @return is SHA-256
     */
    public static boolean isValidSHA256(String hash) {
        // apply regexp
        return hash.matches("[A-Fa-f0-9]{64}");
    }

    /**
     * Validates the hash.
     *
     * @param hash input hash to be validated
     * @return true: if the input hash is valid
     * false: if the input hash is invalid
     */
    public static boolean validateHash(String hash) {
        if (StringUtils.isBlank(hash)) {
            // invalid
            return false;
        }

        if (hash.length() > 256) {
            // invalid
            return false;
        }

        if (isValidMD5(hash)) {
            return true;
        }

        // check for SHA-1
        if (isValidSHA1(hash)) {
            // is valid sha 1 hash
            return true;
        }

        // check for SHA-256
        return isValidSHA256(hash);
    }

    /**
     * Calculates the SHA256 hash from a byte array and returns it in HEX.
     *
     * @param bytes to be converted
     * @return hex string
     * @throws NoSuchAlgorithmException implementation error, used wrong identifier
     */
    public static String getSHA256(byte[] bytes) throws NoSuchAlgorithmException {
        return getHash(bytes, "SHA-256");
    }

    /**
     * Calculates the SHA-1 from given input byte array.
     *
     * @param bytes to be converted
     * @return hex string
     * @throws NoSuchAlgorithmException implementation error, used wrong identifier
     */
    public static String getSHA1(byte[] bytes) throws NoSuchAlgorithmException {
        return getHash(bytes, "SHA-1");
    }

    /**
     * Calculates the MD5 hash from given input byte array.
     *
     * @param bytes to be converted
     * @return hex string
     * @throws NoSuchAlgorithmException implementation error, used wrong identifier
     */
    public static String getMD5(byte[] bytes) throws NoSuchAlgorithmException {
        return getHash(bytes, "MD5");
    }

    private static String getHash(byte[] bytes, String type) throws NoSuchAlgorithmException {
        MessageDigest hash = MessageDigest.getInstance(type);
        hash.update(bytes);
        return convertToHex(hash.digest());
    }

    /**
     * Converts a byte array to its hex string representation.
     *
     * @param bytes to be converted
     * @return hex string
     */
    private static String convertToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        // for each byte
        for (byte singleByte : bytes) {
            String hex = Integer.toHexString(0xff & singleByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * Calculates the SHA256 hash from a BufferedInputStream.
     */
    public static String getSHA256(
            BufferedInputStream inputStream,
            int bufferSize
    ) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[bufferSize];
        int sizeRead = -1;

        while ((sizeRead = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, sizeRead);
        }
        inputStream.close();

        byte[] hash = new byte[digest.getDigestLength()];
        hash = digest.digest();
        return convertToHex(hash);
    }
}
