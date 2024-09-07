package com.jo4ovms.StockifyAPI.model.DTO;

import lombok.Data;

@Data
public class DemandPredictionDTO {
    private Long productId;
    private String productName;
    private int predictedDemand;
    private String predictionPeriod;

    public DemandPredictionDTO() {
    }

    public DemandPredictionDTO(Long productId, String productName, int predictedDemand, String predictionPeriod) {
        this.productId = productId;
        this.productName = productName;
        this.predictedDemand = predictedDemand;
        this.predictionPeriod = predictionPeriod;
    }


}