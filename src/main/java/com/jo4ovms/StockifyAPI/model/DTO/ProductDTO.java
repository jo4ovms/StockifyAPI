package com.jo4ovms.StockifyAPI.model.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Product name cannot be blank.")
    @Size(max = 100, message = "Product name cannot exceed 100 characters.")
    private String name;

    @NotNull(message = "Product value is required.")
    @Positive(message = "Product value must be positive.")
    @DecimalMin("0.0")
    private Double value;

    @NotNull(message = "Product quantity is required.")
    @Positive(message = "Product quantity must be positive.")
    private Integer quantity;

    @NotNull(message = "Supplier ID is required.")
    private Long supplierId;

    private String supplierName;
}
