package com.jo4ovms.StockifyAPI.service;

import com.jo4ovms.StockifyAPI.exception.InsufficientStockException;
import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.exception.ValidationException;
import com.jo4ovms.StockifyAPI.mapper.StockMovementMapper;
import com.jo4ovms.StockifyAPI.model.DTO.StockMovementDTO;
import com.jo4ovms.StockifyAPI.model.Stock;
import com.jo4ovms.StockifyAPI.model.StockMovement;
import com.jo4ovms.StockifyAPI.repository.StockMovementRepository;
import com.jo4ovms.StockifyAPI.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockMovementService {
    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private StockMovementMapper stockMovementMapper;

    @Autowired
    private StockRepository stockRepository;

    public StockMovementDTO registerMovement(StockMovementDTO stockMovementDTO) {
        Stock stock = stockRepository.findById(stockMovementDTO.getStockId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock with id " + stockMovementDTO.getStockId() + " not found."));

        if (stockMovementDTO.getMovementType() == StockMovement.MovementType.OUTBOUND && stock.getQuantity() < stockMovementDTO.getQuantityChange()) {
            throw new InsufficientStockException("Not enough stock available for this movement.");
        }
        if (stockMovementDTO.getQuantityChange() <= 0) {
            throw new ValidationException("Quantity must be greater than zero");
        }

        stock.setQuantity(stock.getQuantity() - stockMovementDTO.getQuantityChange());
        stockRepository.save(stock);

        StockMovement stockMovement = stockMovementMapper.toStockMovement(stockMovementDTO);
        stockMovementRepository.save(stockMovement);

        return stockMovementMapper.toStockMovementDTO(stockMovement);
    }


    public StockMovementDTO getStockMovementById(Long id) {
        StockMovement stockMovement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement with id " + id + " not found."));
        return stockMovementMapper.toStockMovementDTO(stockMovement);
    }


    public List<StockMovementDTO> getAllStockMovements() {
        return stockMovementRepository.findAll()
                .stream()
                .map(stockMovementMapper::toStockMovementDTO)
                .collect(Collectors.toList());
    }
}
