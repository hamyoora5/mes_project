package com.example.mes.controller;

import com.example.mes.common.ApiResponse;
import com.example.mes.dto.ErpResultResponse;
import com.example.mes.service.WorkOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ERP 전송 대상 결과 조회 API의 진입점입니다.
 */
@RestController
@RequestMapping("/erp/results")
public class ErpResultController {

    private final WorkOrderService workOrderService;

    public ErpResultController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    /**
     * ERP 관점에서 필요한 생산 결과 목록을 조회합니다.
     *
     * @return ERP 전송 대상 결과 목록
     */
    @GetMapping
    public ApiResponse<List<ErpResultResponse>> findAll() {
        return ApiResponse.ok(workOrderService.getErpResults());
    }
}
