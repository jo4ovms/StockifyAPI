package com.jo4ovms.StockifyAPI.model.DTO;

public class DemandPredictionDTO {
    private Long productId;
    private String productName;
    private int predictedDemand;
    private String predictionPeriod; // Ex: "Next 30 days"

    public DemandPredictionDTO() {
    }

    public DemandPredictionDTO(Long productId, String productName, int predictedDemand, String predictionPeriod) {
        this.productId = productId;
        this.productName = productName;
        this.predictedDemand = predictedDemand;
        this.predictionPeriod = predictionPeriod;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPredictedDemand() {
        return predictedDemand;
    }

    public void setPredictedDemand(int predictedDemand) {
        this.predictedDemand = predictedDemand;
    }

    public String getPredictionPeriod() {
        return predictionPeriod;
    }

    public void setPredictionPeriod(String predictionPeriod) {
        this.predictionPeriod = predictionPeriod;
    }
}