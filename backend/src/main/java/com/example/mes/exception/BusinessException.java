package com.example.mes.exception;

import org.springframework.http.HttpStatus;

/**
 * 도메인 규칙 위반을 표현하기 위한 애플리케이션 공통 예외입니다.
 *
 * <p>왜 RuntimeException을 바로 쓰지 않고 별도 타입을 두는가:
 * 비즈니스 오류는 "서버 장애"가 아니라 "요청은 도달했지만 규칙상 처리할 수 없음"을
 * 의미합니다. 이 예외는 그런 상황을 의도적으로 구분하기 위해 사용합니다.
 *
 * <p>어떻게 쓰는가:
 * 서비스 계층에서 상태 전이 불가, 데이터 없음, 중복 등록 등 규칙 위반 시 던지고,
 * 전역 예외 처리기에서 HTTP 상태 코드와 메시지로 변환합니다.
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * 이 예외에 매핑된 HTTP 상태 코드를 반환합니다.
     *
     * @return HTTP 상태 코드
     */
    public HttpStatus getStatus() {
        return status;
    }
}
