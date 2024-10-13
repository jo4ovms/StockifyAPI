package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.LogDTO;
import com.jo4ovms.StockifyAPI.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {



    private final LogService logService;
    private final PagedResourcesAssembler<LogDTO> pagedResourcesAssembler;

    public LogController(LogService logService, PagedResourcesAssembler<LogDTO> pagedResourcesAssembler) {
        this.logService = logService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }


    @GetMapping("/recent")
    public ResponseEntity<PagedModel<EntityModel<LogDTO>>> getRecentActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<LogDTO> recentLogs = logService.getRecentActivities(page, size);

        if (recentLogs.hasContent()) {
            PagedModel<EntityModel<LogDTO>> pagedModel = pagedResourcesAssembler.toModel(recentLogs);
            return ResponseEntity.ok(pagedModel);
        } else {
            return ResponseEntity.noContent().build(); // Return 204 if no logs found
        }
    }
}
