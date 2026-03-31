const API_BASE_URL = "http://localhost:8002";

/**
 * 프론트엔드의 모든 백엔드 호출이 거치는 공통 요청 함수입니다.
 *
 * 왜 공통 함수가 필요한가:
 * - API 기본 주소를 한 곳에서 관리할 수 있습니다.
 * - 공통 헤더 처리 방식을 통일할 수 있습니다.
 * - 에러 응답 포맷을 한 번만 해석하면 됩니다.
 *
 * 현재 백엔드는 `ApiResponse<T>` 형태를 사용하므로, 실제 데이터는 `data.data`
 * 안에 들어 있습니다. 이 함수는 그 구조를 풀어서 페이지 코드에서는 비즈니스 데이터만
 * 바로 사용할 수 있게 해 줍니다.
 *
 * @param {string} path 백엔드 엔드포인트 경로
 * @param {RequestInit} [options={}] fetch 옵션
 * @returns {Promise<any>} 백엔드 응답의 실제 data 필드
 * @throws {Error} 서버가 실패 응답을 돌려준 경우 사용자 메시지를 포함한 에러
 */
export async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    },
    ...options
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data?.data?.message || "요청 처리 중 오류가 발생했습니다.");
  }
  return data.data;
}
