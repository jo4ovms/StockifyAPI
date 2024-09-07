package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.DemandPredictionDTO;
import com.jo4ovms.StockifyAPI.service.stock.DemandPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analysis")
@Tag(name = "Demand Prediction", description = "Endpoints for demand prediction analysis")
public class DemandPredictionController {

    private final DemandPredictionService demandPredictionService;

    public DemandPredictionController(DemandPredictionService demandPredictionService) {
        this.demandPredictionService = demandPredictionService;
    }

    @Operation(summary = "Generate demand prediction", description = "Predict the demand for products based on sales history.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prediction generated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping("/predict")
    public ResponseEntity<DemandPredictionDTO> predictDemand(@RequestParam Long productId) {
        DemandPredictionDTO prediction = demandPredictionService.predictDemand(productId);
        return ResponseEntity.ok(prediction);
    }
}