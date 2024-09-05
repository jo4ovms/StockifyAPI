package com.jo4ovms.StockifyAPI.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierDTO {
    private Long id;

    @NotBlank(message = "Supplier name cannot be blank.")
    @Size(max = 100, message = "Supplier name cannot exceed 100 characters.")
    private String name;

    @NotBlank(message = "Supplier Phone cannot be blank.")
    @Size(max = 15, message = "Phone number cannot exceed 15 characters.")
    private String phone;


    @NotBlank(message = "Supplier Email cannot be blank.")
    @Email(message = "Email should be valid.")
    @Size(max = 100, message = "Email cannot exceed 100 characters.")
    private String email;

    @NotBlank(message = "Supplier Product Type cannot be blank.")
    @Size(max = 50, message = "Product type cannot exceed 50 characters.")
    private String productType;
}
