package com.jo4ovms.StockifyAPI.mapper;

import com.jo4ovms.StockifyAPI.model.DTO.ProductDTO;
import com.jo4ovms.StockifyAPI.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "supplier.name", target = "supplierName")
    @Mapping(source = "stock.quantity", target = "quantity")
    ProductDTO toProductDTO(Product product);

    Product toProduct(ProductDTO productDTO);
}
