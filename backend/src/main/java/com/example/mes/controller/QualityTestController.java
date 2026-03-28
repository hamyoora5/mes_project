package com.example.mes.controller;

import com.example.mes.common.ApiResponse;
import com.example.mes.dto.ApplyAiResultRequest;
import com.example.mes.dto.CreateQualityTestRequest;
import com.example.mes.dto.QualityTestResponse;
import com.example.mes.service.QualityTestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quality-tests")
public class QualityTestController {

    private final QualityTestService qualityTestService;

    public QualityTestController(QualityTestService qualityTestService) {
        this.qualityTestService = qualityTestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<QualityTestResponse> create(@Valid @RequestBody CreateQualityTestRequest request) {
        return ApiResponse.ok(qualityTestService.create(request));
    }

    @PostMapping("/{testNo}/ai-result")
    public ApiResponse<QualityTestResponse> applyAiResult(
            @PathVariable String testNo,
            @Valid @RequestBody ApplyAiResultRequest request
    ) {
        return ApiResponse.ok(qualityTestService.applyAiResult(testNo, request));
    }

    @GetMapping("/{testNo}")
    public ApiResponse<QualityTestResponse> findByTestNo(@PathVariable String testNo) {
        return ApiResponse.ok(qualityTestService.findByTestNo(testNo));
    }
}
