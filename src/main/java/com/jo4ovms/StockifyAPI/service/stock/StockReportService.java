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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class StockReportService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final StockMovementMapper stockMovementMapper;

    @Autowired
    public StockReportService(StockRepository stockRepository, StockMapper stockMapper, StockMovementMapper stockMovementMapper) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
        this.stockMovementMapper = stockMovementMapper;
    }


    public Page<StockDTO> generateLowStockReport(int threshold, Pageable pageable) {
        Page<Stock> lowStockProducts = stockRepository.findByQuantityBetween(1, threshold - 1, pageable);
        return lowStockProducts.map(stockMapper::toStockDTO);
    }

    public Page<StockMovementDTO> generateStockMovementReport(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return stockRepository.findStockMovementsByDateRange(startDate, endDate, pageable)
                .map(stockMovementMapper::toStockMovementDTO);
    }

    public Page<StockDTO> generateHighStockReport(int quantity, Pageable pageable) {
        Page<Stock> highStockProducts = stockRepository.findByQuantityGreaterThanEqual(quantity, pageable);
        return highStockProducts.map(stockMapper::toStockDTO);
    }

    public Page<StockDTO> getFilteredCriticalStock(String query, Long supplierId, int threshold, String sortBy, String sortDirection, Pageable pageable) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;


        PageRequest pageRequest;
        if (sortBy.equals("supplier")) {
            pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, "product.supplier.name"));
        } else {
            pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, "quantity"));
        }


        return stockRepository.searchCriticalStockByFilters(query, supplierId, threshold, pageRequest)
                .map(stockMapper::toStockDTO);
    }

    public Page<StockDTO> getOutOfStockProducts(Pageable pageable) {
        Page<Stock> outOfStockProducts = stockRepository.findByQuantityEquals(0, pageable);
        return outOfStockProducts.map(stockMapper::toStockDTO);
    }


}
