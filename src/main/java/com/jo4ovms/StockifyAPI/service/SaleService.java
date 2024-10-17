package com.jo4ovms.StockifyAPI.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.mapper.SaleMapper;
import com.jo4ovms.StockifyAPI.model.DTO.*;
import com.jo4ovms.StockifyAPI.model.Log;
import com.jo4ovms.StockifyAPI.model.Sale;
import com.jo4ovms.StockifyAPI.model.Stock;
import com.jo4ovms.StockifyAPI.repository.AggregatedSaleRepository;
import com.jo4ovms.StockifyAPI.repository.SaleRepository;
import com.jo4ovms.StockifyAPI.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class SaleService {

    private final StockRepository stockRepository;
    private final SaleMapper saleMapper;
    private final SaleRepository saleRepository;
    private final LogService logService;
    private final ObjectMapper objectMapper;
    private final AggregatedSaleService aggregatedSaleService;
    private final AggregatedSaleRepository aggregatedSaleRepository;


    public SaleService(StockRepository stockRepository, SaleMapper saleMapper, SaleRepository saleRepository, LogService logService, ObjectMapper objectMapper, AggregatedSaleService aggregatedSaleService, AggregatedSaleRepository aggregatedSaleRepository) {
        this.stockRepository = stockRepository;
        this.saleMapper = saleMapper;
        this.saleRepository = saleRepository;
        this.logService = logService;
        this.objectMapper = objectMapper;
        this.aggregatedSaleService = aggregatedSaleService;
        this.aggregatedSaleRepository = aggregatedSaleRepository;
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


        aggregatedSaleService.updateAggregatedSales(stock.getId(), stock.getProduct().getId(), saleDTO.getQuantity());

        return saleLogDTO;
    }

    public List<BestSellingItemDTO> getBestSellingItems() {
        return saleRepository.findBestSellingItems();
    }



    public Page<SaleSummaryDTO> getAllSalesGroupedByProduct(String searchTerm, Long supplierId, int page, int size, String sortDirection, LocalDate startDate, LocalDate endDate) {
        Sort sort = Sort.by("totalQuantitySold");
        sort = "asc".equalsIgnoreCase(sortDirection) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(page, size, sort);


        if (startDate != null && endDate != null) {
            return aggregatedSaleRepository.findSalesGroupedByProductAndSupplierAndDate(searchTerm, supplierId, startDate, endDate, pageable);
        } else {
            return aggregatedSaleRepository.findSalesGroupedByProductAndSupplier(searchTerm, supplierId, pageable);
        }
    }

    public List<DailySalesDTO> getSalesGroupedByDay(int month) {
        return saleRepository.findSalesGroupedByDay(month);
    }







}
