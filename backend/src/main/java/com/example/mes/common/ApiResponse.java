package com.example.mes.common;

/**
 * 모든 REST API가 공통으로 사용하는 응답 래퍼입니다.
 *
 * <p>왜 이 타입을 쓰는가:
 * <ul>
 *   <li>성공/실패 응답 구조를 프론트에서 동일하게 처리할 수 있습니다.</li>
 *   <li>페이지별로 응답 스키마가 달라도 `success`와 `data`라는 공통 틀을 유지할 수 있습니다.</li>
 *   <li>예외 처리 결과도 같은 구조로 반환해 UI 쪽 분기 처리를 단순화할 수 있습니다.</li>
 * </ul>
 *
 * <p>어디에 쓰는가:
 * 컨트롤러의 모든 응답, 그리고 전역 예외 처리 응답에 사용됩니다.
 *
 * @param success 요청 성공 여부
 * @param data 실제 응답 페이로드 또는 오류 메시지/오류 맵
 * @param <T> 응답 데이터 타입
 */
public record ApiResponse<T>(
        boolean success,
        T data
) {

    /**
     * 성공 응답을 생성합니다.
     *
     * <p>일반 컨트롤러 메서드에서 비즈니스 데이터를 응답으로 내릴 때 사용합니다.
     *
     * @param data 응답 데이터
     * @return 성공 응답 객체
     * @param <T> 응답 데이터 타입
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data);
    }

    /**
     * 실패 응답을 생성합니다.
     *
     * <p>주로 전역 예외 처리기에서 사용자에게 전달할 오류 정보를 감싸는 데 사용합니다.
     *
     * @param data 오류 데이터
     * @return 실패 응답 객체
     * @param <T> 응답 데이터 타입
     */
    public static <T> ApiResponse<T> fail(T data) {
        return new ApiResponse<>(false, data);
    }
}
