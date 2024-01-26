package com.ht.elearning.config;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryResponse<T> {
    private long total;
    private long took;
    private int status;
    private String message;
    private boolean success;
    private T data;
}
