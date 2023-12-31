package com.ht.elearning.utils;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class MultipartFileConverter {
    public static FileSystemResource convert(MultipartFile multipartFile) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            multipartFile.transferTo(tempFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new FileSystemResource(tempFile);
    }
}
