package com.ht.elearning.utils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

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

    public static boolean isSameDay(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return localDate1.equals(localDate2);
    }
}
