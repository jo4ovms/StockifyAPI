package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.StockDTO;
import com.jo4ovms.StockifyAPI.service.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class StockStreamingController {

    @Autowired
    private StockService stockService;

    @GetMapping(value = "/api/stock/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StockDTO> streamAllStocks() {

        return Flux.fromIterable(stockService.getAllStocksNonPaged());

    }
}
