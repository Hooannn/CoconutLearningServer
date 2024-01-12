package com.ht.elearning.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class Helper {
    public static String generateRandomSecret(int length) {
        byte[] randomBytes = new byte[length];
        new SecureRandom().nextBytes(randomBytes);

        // Use Base64 encoding to represent the random bytes as a string
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static boolean isEmail(String str) {
        return str.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }
}
