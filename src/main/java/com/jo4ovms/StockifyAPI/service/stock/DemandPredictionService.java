package com.jo4ovms.StockifyAPI.service.stock;

import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.model.DTO.DemandPredictionDTO;
import com.jo4ovms.StockifyAPI.model.Product;
import com.jo4ovms.StockifyAPI.model.StockMovement;
import com.jo4ovms.StockifyAPI.repository.ProductRepository;
import com.jo4ovms.StockifyAPI.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemandPredictionService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    public DemandPredictionDTO predictDemand(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found."));

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);

        List<StockMovement> stockMovements = stockMovementRepository.findByStockProductIdAndMovementDateBetween(productId, startDate, endDate);

        int totalMovements = stockMovements.stream()
                .mapToInt(StockMovement::getQuantityChange)
                .sum();

        DemandPredictionDTO demandPredictionDTO = new DemandPredictionDTO();
        demandPredictionDTO.setProductId(productId);
        demandPredictionDTO.setPredictedDemand(totalMovements);

        return demandPredictionDTO;
    }
}