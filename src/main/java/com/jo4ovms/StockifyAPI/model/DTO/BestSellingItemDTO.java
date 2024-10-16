package com.jo4ovms.StockifyAPI.model.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BestSellingItemDTO {

    private String productName;
    private Long totalQuantitySold;
}
