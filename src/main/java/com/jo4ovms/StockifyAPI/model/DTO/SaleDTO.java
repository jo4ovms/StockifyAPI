package com.jo4ovms.StockifyAPI.model.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SaleDTO {

    @NotNull(message = "Product ID is required.")
    private Long productId;

    @NotNull(message = "Sale Quantity is required.")
    @Positive(message = "Sale Quantity must be greater than zero.")
    private Integer quantity;

    @NotNull(message = "Stock ID is required.")
    private Long stockId;

    private String productName;
}
