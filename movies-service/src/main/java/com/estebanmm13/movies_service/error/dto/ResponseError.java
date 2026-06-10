package com.estebanmm13.movies_service.error.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResponseError {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
