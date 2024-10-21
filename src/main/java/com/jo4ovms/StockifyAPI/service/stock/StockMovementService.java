package com.jo4ovms.StockifyAPI.service.stock;

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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class StockMovementService {
    private final StockMovementRepository stockMovementRepository;
    private final StockMovementMapper stockMovementMapper;
    private final StockRepository stockRepository;

    @Autowired
    public StockMovementService(StockMovementRepository stockMovementRepository, StockMovementMapper stockMovementMapper, StockRepository stockRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockMovementMapper = stockMovementMapper;
        this.stockRepository = stockRepository;
    }

   // @CacheEvict(value = "stockMovements", key = "#id")
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


   // @Cacheable(value = "stockMovements", key = "#id")
    public StockMovementDTO getStockMovementById(Long id) {
        StockMovement stockMovement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement with id " + id + " not found."));
        return stockMovementMapper.toStockMovementDTO(stockMovement);
    }


    public Page<StockMovementDTO> getAllStockMovements(Pageable pageable) {
        return stockMovementRepository.findAll(pageable)
                .map(stockMovementMapper::toStockMovementDTO);
    }
}
