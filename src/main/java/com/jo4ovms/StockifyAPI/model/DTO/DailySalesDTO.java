package com.jo4ovms.StockifyAPI.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailySalesDTO {

    private Integer day;
    private Long totalSales;
}
