package com.jo4ovms.StockifyAPI.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "stock_id", foreignKey = @ForeignKey(name = "fk_aggregated_sale_stock"), nullable = true)
    private Stock stock;

    @ManyToOne
    private Product product;


    private Long totalQuantitySold;

    private LocalDate saleDate;
}
