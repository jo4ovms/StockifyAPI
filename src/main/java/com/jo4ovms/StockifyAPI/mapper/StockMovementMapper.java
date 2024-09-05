package com.jo4ovms.StockifyAPI.mapper;

import com.jo4ovms.StockifyAPI.model.DTO.StockMovementDTO;
import com.jo4ovms.StockifyAPI.model.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {

    @Mapping(source = "stock.product.name", target = "productName")
    StockMovementDTO toStockMovementDTO(StockMovement stockMovement);

    StockMovement toStockMovement(StockMovementDTO stockMovementDTO);
}