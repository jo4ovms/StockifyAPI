package com.jo4ovms.StockifyAPI;

import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.model.DTO.DemandPredictionDTO;
import com.jo4ovms.StockifyAPI.model.Product;
import com.jo4ovms.StockifyAPI.model.StockMovement;
import com.jo4ovms.StockifyAPI.repository.ProductRepository;
import com.jo4ovms.StockifyAPI.repository.StockMovementRepository;
import com.jo4ovms.StockifyAPI.service.stock.DemandPredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class DemandPredictionServiceTest {

@Mock
private ProductRepository productRepository;

@Mock
private StockMovementRepository stockMovementRepository;

@InjectMocks
private DemandPredictionService demandPredictionService;

@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
}

@Test
void testPredictDemand() {
    // Mock do produto
    Long productId = 1L;
    Product product = new Product();
    product.setId(productId);
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));

    // Mock das movimentações de estoque
    StockMovement movement1 = new StockMovement();
    movement1.setQuantityChange(10);

    StockMovement movement2 = new StockMovement();
    movement2.setQuantityChange(20);

    List<StockMovement> movements = Arrays.asList(movement1, movement2);
    when(stockMovementRepository.findByStockProductIdAndMovementDateBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(movements);

    // Teste do método
    DemandPredictionDTO result = demandPredictionService.predictDemand(productId);

    // Verificações
    assertEquals(productId, result.getProductId());
    assertEquals(30, result.getPredictedDemand()); // 10 + 20 = 30
}

@Test
void testPredictDemandProductNotFound() {
    Long productId = 1L;
    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> demandPredictionService.predictDemand(productId));
}
}