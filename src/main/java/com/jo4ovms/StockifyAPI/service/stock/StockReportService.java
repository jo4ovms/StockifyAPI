package com.jo4ovms.StockifyAPI.service.stock;

import com.jo4ovms.StockifyAPI.mapper.StockMapper;
import com.jo4ovms.StockifyAPI.mapper.StockMovementMapper;
import com.jo4ovms.StockifyAPI.model.DTO.StockDTO;
import com.jo4ovms.StockifyAPI.model.DTO.StockMovementDTO;
import com.jo4ovms.StockifyAPI.model.Stock;
import com.jo4ovms.StockifyAPI.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class StockReportService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockMovementMapper stockMovementMapper;


   // @Cacheable(value = "lowStockReport", key = "#threshold")
    public List<StockDTO> generateLowStockReport(int threshold) {
        List<Stock> lowStockProducts = stockRepository.findByQuantityLessThan(threshold);
        return lowStockProducts.stream()
                .map(stockMapper::toStockDTO)
                .toList();
    }

   // @Cacheable(value = "stockMovementReport", key = "#startDate.toString() + '-' + #endDate.toString()")
    public Page<StockMovementDTO> generateStockMovementReport(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return stockRepository.findStockMovementsByDateRange(startDate, endDate, pageable)
                .map(stockMovementMapper::toStockMovementDTO);
    }


    //@Cacheable(value = "highStockReport", key = "#quantity")
    public List<StockDTO> generateHighStockReport(int quantity) {
        List<Stock> highStockProducts = stockRepository.findByQuantityGreaterThan(quantity);
        return highStockProducts.stream()
                .map(stockMapper::toStockDTO)
                .toList();
    }
    public List<StockDTO> getOutOfStockProducts() {
        List<Stock> outOfStockProducts = stockRepository.findByQuantityEquals(0);
        return outOfStockProducts.stream()
                .map(stockMapper::toStockDTO)
                .toList();
    }

}
