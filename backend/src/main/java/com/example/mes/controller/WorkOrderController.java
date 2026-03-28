package com.example.mes.controller;

import com.example.mes.common.ApiResponse;
import com.example.mes.dto.CreateWorkOrderRequest;
import com.example.mes.dto.ProductionRunHistoryResponse;
import com.example.mes.dto.RecordProductionRequest;
import com.example.mes.dto.RegisterDefectRequest;
import com.example.mes.dto.StopWorkOrderRequest;
import com.example.mes.dto.WorkOrderResponse;
import com.example.mes.service.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WorkOrderResponse> create(@Valid @RequestBody CreateWorkOrderRequest request) {
        return ApiResponse.ok(workOrderService.create(request));
    }

    @GetMapping
    public ApiResponse<List<WorkOrderResponse>> findAll() {
        return ApiResponse.ok(workOrderService.findAll());
    }

    @GetMapping("/{id}/history")
    public ApiResponse<List<ProductionRunHistoryResponse>> getHistory(@PathVariable Long id) {
        return ApiResponse.ok(workOrderService.getHistory(id));
    }

    @PostMapping("/{id}/start")
    public ApiResponse<WorkOrderResponse> start(@PathVariable Long id) {
        return ApiResponse.ok(workOrderService.start(id));
    }

    @PostMapping("/{id}/production")
    public ApiResponse<WorkOrderResponse> recordProduction(@PathVariable Long id, @Valid @RequestBody RecordProductionRequest request) {
        return ApiResponse.ok(workOrderService.recordProduction(id, request));
    }

    @PostMapping("/{id}/defects")
    public ApiResponse<WorkOrderResponse> registerDefect(@PathVariable Long id, @Valid @RequestBody RegisterDefectRequest request) {
        return ApiResponse.ok(workOrderService.registerDefect(id, request));
    }

    @PostMapping("/{id}/stop")
    public ApiResponse<WorkOrderResponse> stop(@PathVariable Long id, @Valid @RequestBody StopWorkOrderRequest request) {
        return ApiResponse.ok(workOrderService.stop(id, request));
    }

    @PostMapping("/{id}/resume")
    public ApiResponse<WorkOrderResponse> resume(@PathVariable Long id) {
        return ApiResponse.ok(workOrderService.resume(id));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<WorkOrderResponse> complete(@PathVariable Long id) {
        return ApiResponse.ok(workOrderService.complete(id));
    }
}
