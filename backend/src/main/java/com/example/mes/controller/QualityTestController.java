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

/**
 * 품질 검사와 AI 판정 반영 API의 진입점입니다.
 */
@RestController
@RequestMapping("/quality-tests")
public class QualityTestController {

    private final QualityTestService qualityTestService;

    public QualityTestController(QualityTestService qualityTestService) {
        this.qualityTestService = qualityTestService;
    }

    /**
     * 일일생산코드를 기준으로 품질 검사 대상을 생성합니다.
     *
     * @param request 품질 검사 등록 요청
     * @return 생성된 품질 검사 정보
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<QualityTestResponse> create(@Valid @RequestBody CreateQualityTestRequest request) {
        return ApiResponse.ok(qualityTestService.create(request));
    }

    /**
     * 특정 품질 검사에 AI 판정 결과를 반영합니다.
     *
     * @param testNo 품질 검사 번호
     * @param request AI 판정 결과 요청
     * @return 결과가 반영된 품질 검사 정보
     */
    @PostMapping("/{testNo}/ai-result")
    public ApiResponse<QualityTestResponse> applyAiResult(
            @PathVariable String testNo,
            @Valid @RequestBody ApplyAiResultRequest request
    ) {
        return ApiResponse.ok(qualityTestService.applyAiResult(testNo, request));
    }

    /**
     * 품질 검사 번호로 검사 결과를 조회합니다.
     *
     * @param testNo 품질 검사 번호
     * @return 품질 검사 조회 결과
     */
    @GetMapping("/{testNo}")
    public ApiResponse<QualityTestResponse> findByTestNo(@PathVariable String testNo) {
        return ApiResponse.ok(qualityTestService.findByTestNo(testNo));
    }
}
