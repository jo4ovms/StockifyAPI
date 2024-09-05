package com.jo4ovms.StockifyAPI.model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class StockDTO {

    private Long id;

    @NotNull(message = "Stock quantity is required.")
    @PositiveOrZero(message = "Stock quantity must be zero or positive.")
    private Integer quantity;

    private boolean available;

    @NotNull(message = "Stock value is required")
    @PositiveOrZero(message = "Stock value must be zero or positive")
    private Double value;


    @NotNull(message = "Product ID is required.")
    private Long productId;

    private String productName;
}
