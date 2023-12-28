package com.ht.elearning.utils;

import java.io.*;
import java.security.*;
public class MD5 {
    public static String hex(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(Integer.toHexString((b
                    & 0xFF) | 0x100), 1, 3);
        }
        return sb.toString();
    }
    public static String md5Hex (String message) {
        try {
            MessageDigest md =
                    MessageDigest.getInstance("MD5");
            return hex (md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}