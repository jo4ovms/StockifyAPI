package com.jo4ovms.StockifyAPI.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.mapper.SaleMapper;
import com.jo4ovms.StockifyAPI.model.DTO.BestSellingItemDTO;
import com.jo4ovms.StockifyAPI.model.DTO.LogDTO;
import com.jo4ovms.StockifyAPI.model.DTO.SaleDTO;
import com.jo4ovms.StockifyAPI.model.Log;
import com.jo4ovms.StockifyAPI.model.Sale;
import com.jo4ovms.StockifyAPI.model.Stock;
import com.jo4ovms.StockifyAPI.repository.ProductRepository;
import com.jo4ovms.StockifyAPI.repository.SaleRepository;
import com.jo4ovms.StockifyAPI.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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


}
