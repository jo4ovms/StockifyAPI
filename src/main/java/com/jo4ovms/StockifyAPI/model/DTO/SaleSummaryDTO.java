package com.jo4ovms.StockifyAPI.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaleSummaryDTO {
    private String productName;
    private Long totalQuantitySold;
}
