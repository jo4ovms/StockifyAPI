package com.jo4ovms.StockifyAPI.mapper;

import com.jo4ovms.StockifyAPI.model.DTO.StockDTO;
import com.jo4ovms.StockifyAPI.model.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(source = "product.name", target = "productName")
    StockDTO toStockDTO(Stock stock);

    Stock toStock(StockDTO stockDTO);
}