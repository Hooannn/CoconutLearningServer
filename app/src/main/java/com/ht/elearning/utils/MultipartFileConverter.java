package com.ht.elearning.utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

public class MultipartFileConverter {

    public static Resource convert(MultipartFile multipartFile) {
        try {
            String originalFilename = Objects.requireNonNullElse(
                    multipartFile.getOriginalFilename(),
                    Helper.generateRandomSecret(10)
            );

            byte[] content = multipartFile.getBytes();
            return new ByteArrayResource(content) {
                @Override
                public String getFilename() {
                    return originalFilename;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
