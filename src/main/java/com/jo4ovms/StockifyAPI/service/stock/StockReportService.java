package com.jo4ovms.StockifyAPI.service.stock;

import com.jo4ovms.StockifyAPI.mapper.StockMapper;
import com.jo4ovms.StockifyAPI.mapper.StockMovementMapper;
import com.jo4ovms.StockifyAPI.model.DTO.StockDTO;
import com.jo4ovms.StockifyAPI.model.DTO.StockMovementDTO;

import com.jo4ovms.StockifyAPI.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
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
    public Page<StockMovementDTO> generateStockMovementReport(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return stockRepository.findStockMovementsByDateRange(startDate, endDate, pageable)
                .map(stockMovementMapper::toStockMovementDTO);
    }

    public Page<StockDTO> getFilteredAdequateStock(String query, Long supplierId, int threshold, String sortBy, String sortDirection, Pageable pageable) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;


        PageRequest pageRequest;
        if (sortBy.equals("supplier")) {
            pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, "product.supplier.name"));
        } else {
            pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, "quantity"));
        }


        return stockRepository.searchAdequateStockByFilters(query, supplierId, threshold, pageRequest)
                .map(stockMapper::toStockDTO);
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

    public Page<StockDTO> getFilteredLowStock(String query, Long supplierId, int threshold, String sortBy, String sortDirection, Pageable pageable) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortBy));


        return stockRepository.searchLowStockByFilters(query, supplierId, 1, threshold - 1, pageRequest)
                .map(stockMapper::toStockDTO);
    }


    public Page<StockDTO> getFilteredOutOfStock(String query, Long supplierId, Pageable pageable) {
        return stockRepository.searchOutOfStockByFilters(query, supplierId, pageable)
                .map(stockMapper::toStockDTO);
    }


}
