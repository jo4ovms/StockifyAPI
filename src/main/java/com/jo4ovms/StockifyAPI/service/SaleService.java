package com.jo4ovms.StockifyAPI.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.mapper.SaleMapper;
import com.jo4ovms.StockifyAPI.model.DTO.BestSellingItemDTO;
import com.jo4ovms.StockifyAPI.model.DTO.LogDTO;
import com.jo4ovms.StockifyAPI.model.DTO.SaleDTO;
import com.jo4ovms.StockifyAPI.model.DTO.SaleSummaryDTO;
import com.jo4ovms.StockifyAPI.model.Log;
import com.jo4ovms.StockifyAPI.model.Sale;
import com.jo4ovms.StockifyAPI.model.Stock;
import com.jo4ovms.StockifyAPI.repository.SaleRepository;
import com.jo4ovms.StockifyAPI.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class SaleService {

    private final StockRepository stockRepository;
    private final SaleMapper saleMapper;
    private final SaleRepository saleRepository;
    private final LogService logService;
    private final ObjectMapper objectMapper;

    public SaleService(StockRepository stockRepository, SaleMapper saleMapper, SaleRepository saleRepository, LogService logService, ObjectMapper objectMapper) {
        this.stockRepository = stockRepository;
        this.saleMapper = saleMapper;
        this.saleRepository = saleRepository;
        this.logService = logService;
        this.objectMapper = objectMapper;
    }

    public SaleDTO registerSale(SaleDTO saleDTO) {


        Stock stock = stockRepository.findById(saleDTO.getStockId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock with id " + saleDTO.getStockId() + " not found"));


        if (stock.getQuantity() < saleDTO.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Requested quantity exceeds available stock");
        }


        stock.setQuantity(stock.getQuantity() - saleDTO.getQuantity());
        stock.setAvailable(stock.getQuantity() > 0);
        stockRepository.save(stock);


        Sale sale = saleMapper.toSale(saleDTO);
        sale.setProduct(stock.getProduct());
        sale.setStock(stock);

        Sale savedSale = saleRepository.save(sale);


        String productName = stock.getProduct().getName();


        SaleDTO saleLogDTO = saleMapper.toSaleDTO(savedSale);
        saleLogDTO.setProductId(stock.getProduct().getId());
        saleLogDTO.setProductName(productName);


        LogDTO logDTO = new LogDTO();
        logDTO.setTimestamp(savedSale.getSaleDate());
        logDTO.setEntity("Sale");
        logDTO.setEntityId(savedSale.getId());
        logDTO.setOperationType(Log.OperationType.CREATE.toString());
        logDTO.setDetails("Sale registered: Stock ID " + stock.getId() + ", Product: " + productName + ", Quantity: " + saleDTO.getQuantity());


        try {
            String newValueJson = objectMapper.writeValueAsString(saleLogDTO);
            logDTO.setNewValue(newValueJson);
        } catch (Exception e) {
            e.printStackTrace();
            logDTO.setNewValue("Error serializing new value");
        }


        logService.createLog(logDTO);

        return saleLogDTO;
    }

    public List<BestSellingItemDTO> getBestSellingItems() {
        return saleRepository.findBestSellingItems();
    }



    public Page<SaleSummaryDTO> getAllSalesGroupedByProduct(String searchTerm, Long supplierId, Pageable pageable, String sortDirection) {
        List<SaleSummaryDTO> results = saleRepository.findSalesGroupedByProductAndSupplier(searchTerm, supplierId);


        if ("asc".equalsIgnoreCase(sortDirection)) {
            results.sort(Comparator.comparing(SaleSummaryDTO::getTotalQuantitySold));
        } else {
            results.sort(Comparator.comparing(SaleSummaryDTO::getTotalQuantitySold).reversed());
        }


        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, results.size());

        List<SaleSummaryDTO> pageContent;
        if (fromIndex > results.size()) {
            pageContent = Collections.emptyList();
        } else {
            pageContent = results.subList(fromIndex, toIndex);
        }

        return new PageImpl<>(pageContent, pageable, results.size());
    }







}
