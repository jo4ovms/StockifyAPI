package com.jo4ovms.StockifyAPI.model.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jo4ovms.StockifyAPI.model.StockMovement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockMovementDTO {

    private Long id;

    @NotNull(message = "Stock ID is required.")
    private Long stockId;

    @NotNull(message = "Quantity change is required.")
    @PositiveOrZero(message = "Quantity change must be zero or positive for inbound movements.")
    private Integer quantityChange;

    @NotNull(message = "Movement type is required.")
    private StockMovement.MovementType movementType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime movementDate;

    @NotBlank(message = "Product name cannot be blank.")
    @Size(max = 100, message = "Product name cannot exceed 100 characters.")
    private String productName;
}
