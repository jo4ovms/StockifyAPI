package com.jo4ovms.StockifyAPI.mapper;

import com.jo4ovms.StockifyAPI.model.DTO.StockDTO;
import com.jo4ovms.StockifyAPI.model.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.supplier.name", target = "supplierName")
    @Mapping(source = "product.supplier.id", target = "supplierId")
    StockDTO toStockDTO(Stock stock);

    Stock toStock(StockDTO stockDTO);
}