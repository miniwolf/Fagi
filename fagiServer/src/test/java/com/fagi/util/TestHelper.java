package com.fagi.util;

import java.util.Random;

public class TestHelper {
    private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    /**
     * Create a random string
     *
     * @param length the required length of the string
     * @return random string with the required length.
     * @throws IllegalArgumentException if the length is less then 1.
     */
    public static String getSaltString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException(length + ": is not a valid length. Must be larger than 1.");
        }

        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();

        while (salt.length() < length) {
            int index = (int) (rnd.nextFloat() * SALT_CHARS.length());
            salt.append(SALT_CHARS.charAt(index));
        }
        return salt.toString();
    }
}
