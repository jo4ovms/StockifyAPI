package com.jo4ovms.StockifyAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private boolean available;

    @Column(nullable = false, name = "stock_value")
    private Double value;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
