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

/**
 * 작업지시와 생산 실행 API의 진입점입니다.
 *
 * <p>작업지시 등록, 조회, 시작, 생산량 입력, 불량 등록,
 * 중단, 재가동, 종료, 이력 조회 기능을 제공합니다.
 */
@RestController
@RequestMapping("/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    /**
     * 새로운 작업지시를 등록합니다.
     *
     * @param request 작업지시 등록 요청
     * @return 등록된 작업지시 응답
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WorkOrderResponse> create(@Valid @RequestBody CreateWorkOrderRequest request) {
        return ApiResponse.ok(workOrderService.create(request));
    }

    /**
     * 전체 작업지시 목록을 조회합니다.
     *
     * @return 작업지시 목록
     */
    @GetMapping
    public ApiResponse<List<WorkOrderResponse>> findAll() {
        return ApiResponse.ok(workOrderService.findAll());
    }

    /**
     * 특정 작업지시의 생산 이력을 조회합니다.
     *
     * @param id 작업지시 ID
     * @return 생산 실행 이력 목록
     */
    @GetMapping("/{id}/history")
    public ApiResponse<List<ProductionRunHistoryResponse>> getHistory(@PathVariable Long id) {
        return ApiResponse.ok(workOrderService.getHistory(id));
    }

    /**
     * 작업을 시작하고 일일생산코드를 발급합니다.
     *
     * @param id 작업지시 ID
     * @return 상태가 갱신된 작업지시
     */
    @PostMapping("/{id}/start")
    public ApiResponse<WorkOrderResponse> start(@PathVariable Long id) {
        return ApiResponse.ok(workOrderService.start(id));
    }

    /**
     * 진행 중인 작업에 생산 수량을 누적합니다.
     *
     * @param id 작업지시 ID
     * @param request 생산량 입력 요청
     * @return 상태가 반영된 작업지시
     */
    @PostMapping("/{id}/production")
    public ApiResponse<WorkOrderResponse> recordProduction(@PathVariable Long id, @Valid @RequestBody RecordProductionRequest request) {
        return ApiResponse.ok(workOrderService.recordProduction(id, request));
    }

    /**
     * 진행 중인 작업에 불량 수량을 누적하고 불량 이력을 저장합니다.
     *
     * @param id 작업지시 ID
     * @param request 불량 등록 요청
     * @return 상태가 반영된 작업지시
     */
    @PostMapping("/{id}/defects")
    public ApiResponse<WorkOrderResponse> registerDefect(@PathVariable Long id, @Valid @RequestBody RegisterDefectRequest request) {
        return ApiResponse.ok(workOrderService.registerDefect(id, request));
    }

    /**
     * 작업을 중단합니다.
     *
     * @param id 작업지시 ID
     * @param request 중단 사유 요청
     * @return 상태가 갱신된 작업지시
     */
    @PostMapping("/{id}/stop")
    public ApiResponse<WorkOrderResponse> stop(@PathVariable Long id, @Valid @RequestBody StopWorkOrderRequest request) {
        return ApiResponse.ok(workOrderService.stop(id, request));
    }

    /**
     * 중단된 작업을 재가동합니다.
     *
     * @param id 작업지시 ID
     * @return 상태가 갱신된 작업지시
     */
    @PostMapping("/{id}/resume")
    public ApiResponse<WorkOrderResponse> resume(@PathVariable Long id) {
        return ApiResponse.ok(workOrderService.resume(id));
    }

    /**
     * 진행 중인 작업을 종료하고 생산 결과를 집계합니다.
     *
     * @param id 작업지시 ID
     * @return 완료 상태로 갱신된 작업지시
     */
    @PostMapping("/{id}/complete")
    public ApiResponse<WorkOrderResponse> complete(@PathVariable Long id) {
        return ApiResponse.ok(workOrderService.complete(id));
    }
}
