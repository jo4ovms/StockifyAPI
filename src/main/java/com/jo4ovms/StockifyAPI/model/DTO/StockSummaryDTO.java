package com.jo4ovms.StockifyAPI.model.DTO;

public class StockSummaryDTO {
    private long totalProducts;
    private long zeroQuantity;
    private long aboveThreshold;
    private long betweenThreshold;


    public StockSummaryDTO(long totalProducts, long zeroQuantity, long aboveThreshold, long betweenThreshold) {
        this.totalProducts = totalProducts;
        this.zeroQuantity = zeroQuantity;
        this.aboveThreshold = aboveThreshold;
        this.betweenThreshold = betweenThreshold;
    }


    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getZeroQuantity() {
        return zeroQuantity;
    }

    public void setZeroQuantity(long zeroQuantity) {
        this.zeroQuantity = zeroQuantity;
    }

    public long getAboveThreshold() {
        return aboveThreshold;
    }

    public void setAboveThreshold(long aboveThreshold) {
        this.aboveThreshold = aboveThreshold;
    }

    public long getBetweenThreshold() {
        return betweenThreshold;
    }

    public void setBetweenThreshold(long betweenThreshold) {
        this.betweenThreshold = betweenThreshold;
    }
}
