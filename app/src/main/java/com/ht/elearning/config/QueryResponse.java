package com.ht.elearning.config;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryResponse<T> {
    private int total;
    private int took;
    private int status;
    private String message;
    private boolean success;
    private T data;
}
