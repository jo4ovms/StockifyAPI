package com.jo4ovms.StockifyAPI.mapper;

import com.jo4ovms.StockifyAPI.model.DTO.SaleDTO;
import com.jo4ovms.StockifyAPI.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    @Mapping(source = "stock.id", target = "stockId")
    @Mapping(source = "product.name", target = "productName")
    SaleDTO toSaleDTO(Sale sale);

    Sale toSale(SaleDTO saleDTO);
}
