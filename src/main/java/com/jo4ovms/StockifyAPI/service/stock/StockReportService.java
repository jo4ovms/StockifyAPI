package com.jo4ovms.StockifyAPI.service.stock;

import com.jo4ovms.StockifyAPI.mapper.StockMapper;
import com.jo4ovms.StockifyAPI.mapper.StockMovementMapper;
import com.jo4ovms.StockifyAPI.model.DTO.StockDTO;
import com.jo4ovms.StockifyAPI.model.DTO.StockMovementDTO;
import com.jo4ovms.StockifyAPI.model.Stock;
import com.jo4ovms.StockifyAPI.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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


    public List<StockDTO> generateLowStockReport(int threshold) {
        List<Stock> lowStockProducts = stockRepository.findByQuantityLessThan(threshold);
        return lowStockProducts.stream()
                .map(stockMapper::toStockDTO)
                .toList();
    }

    public List<StockMovementDTO> generateStockMovementReport(LocalDate startDate, LocalDate endDate) {
        return stockRepository.findStockMovementsByDateRange(startDate, endDate)
                .stream()
                .map(stockMovementMapper::toStockMovementDTO)
                .toList();
    }
    public List<StockDTO> generateHighStockReport(int quantity) {
        List<Stock> highStockProducts = stockRepository.findByQuantityGreaterThan(quantity);
        return highStockProducts.stream()
                .map(stockMapper::toStockDTO)
                .toList();
    }
}
