package com.jo4ovms.StockifyAPI.service.stock;

import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.model.DTO.DemandPredictionDTO;
import com.jo4ovms.StockifyAPI.model.DTO.StockMovementDTO;
import com.jo4ovms.StockifyAPI.model.Product;
import com.jo4ovms.StockifyAPI.model.StockMovement;
import com.jo4ovms.StockifyAPI.repository.ProductRepository;
import com.jo4ovms.StockifyAPI.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemandPredictionService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    public DemandPredictionDTO predictDemand(Long productId) {
        // Verifica se o produto existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found."));

        // Define o intervalo de datas para a previsão (por exemplo, os últimos 30 dias)
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);

        // Recupera as movimentações de estoque para o produto no intervalo de datas
        List<StockMovement> stockMovements = stockMovementRepository.findByStockProductIdAndMovementDateBetween(productId, startDate, endDate);

        // Implementa lógica para calcular a previsão com base nas movimentações (exemplo básico)
        int totalMovements = stockMovements.stream()
                .mapToInt(StockMovement::getQuantityChange)
                .sum();

        // Cria o DTO da previsão de demanda
        DemandPredictionDTO demandPredictionDTO = new DemandPredictionDTO();
        demandPredictionDTO.setProductId(productId);
        demandPredictionDTO.setPredictedDemand(totalMovements);  // Exemplo básico, ajustar conforme a necessidade

        return demandPredictionDTO;
    }
}