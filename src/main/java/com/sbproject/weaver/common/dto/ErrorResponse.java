package com.sbproject.weaver.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String message;
    private String details;

    public static ErrorResponse fail(
            int status,
            String message,
            String details
    ) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status)
                .message(message)
                .details(details)
                .build();
    }
}