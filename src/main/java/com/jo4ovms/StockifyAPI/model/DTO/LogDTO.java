package com.jo4ovms.StockifyAPI.model.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogDTO {

    private Long id;

    @NotNull
    private LocalDateTime timestamp;

    private Long userId;

    @NotNull
    private String operationType;

    @NotNull
    private String entity;

    @NotNull
    private Long entityId;

    private String oldValue;

    private String newValue;

    private String details;
}
