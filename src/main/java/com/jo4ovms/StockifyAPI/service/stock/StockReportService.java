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


    public Page<StockDTO> generateLowStockReport(int threshold, Pageable pageable) {
        Page<Stock> lowStockProducts = stockRepository.findByQuantityLessThan(threshold, pageable);
        return lowStockProducts.map(stockMapper::toStockDTO);
    }

    public Page<StockMovementDTO> generateStockMovementReport(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return stockRepository.findStockMovementsByDateRange(startDate, endDate, pageable)
                .map(stockMovementMapper::toStockMovementDTO);
    }

    public Page<StockDTO> generateHighStockReport(int quantity, Pageable pageable) {
        Page<Stock> highStockProducts = stockRepository.findByQuantityGreaterThan(quantity, pageable);
        return highStockProducts.map(stockMapper::toStockDTO);
    }

    public List<StockDTO> getOutOfStockProducts() {
        List<Stock> outOfStockProducts = stockRepository.findByQuantityEquals(0);
        return outOfStockProducts.stream()
                .map(stockMapper::toStockDTO)
                .toList();
    }

}
