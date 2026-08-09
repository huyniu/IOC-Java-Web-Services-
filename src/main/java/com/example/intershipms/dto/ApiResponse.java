package com.example.intershipms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<ErrorDetail> errors;
    private LocalDateTime timestamp;

    @Data
    @Builder
    public static class ErrorDetail {
        private String field;
        private String message;
    }
}