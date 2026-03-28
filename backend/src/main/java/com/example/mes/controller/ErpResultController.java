package com.example.mes.controller;

import com.example.mes.common.ApiResponse;
import com.example.mes.dto.ErpResultResponse;
import com.example.mes.service.WorkOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/erp/results")
public class ErpResultController {

    private final WorkOrderService workOrderService;

    public ErpResultController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping
    public ApiResponse<List<ErpResultResponse>> findAll() {
        return ApiResponse.ok(workOrderService.getErpResults());
    }
}
