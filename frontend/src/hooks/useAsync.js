import { useEffect, useState } from "react";

/**
 * 비동기 데이터를 로딩/에러/성공 상태와 함께 다루기 위한 공통 훅입니다.
 *
 * 왜 사용하는가:
 * - 각 페이지에서 fetch 상태 관리 코드를 반복하지 않기 위해서
 * - 로딩, 데이터, 에러의 3상태를 동일한 구조로 다루기 위해서
 *
 * 현재 구현은 단순 조회 화면에 맞춘 최소 형태입니다. 의존성이 바뀌면 다시 요청하고,
 * 컴포넌트가 언마운트되면 뒤늦은 setState를 막기 위해 active 플래그를 둡니다.
 *
 * @param {() => Promise<any>} asyncFn 실행할 비동기 함수
 * @param {Array<any>} deps useEffect 의존성 배열
 * @returns {{data: any, loading: boolean, error: string|null}} 비동기 상태
 */
export function useAsync(asyncFn, deps) {
  const [state, setState] = useState({
    data: null,
    loading: true,
    error: null
  });

  useEffect(() => {
    let active = true;
    setState({ data: null, loading: true, error: null });
    asyncFn()
      .then((data) => {
        if (active) {
          setState({ data, loading: false, error: null });
        }
      })
      .catch((error) => {
        if (active) {
          setState({ data: null, loading: false, error: error.message });
        }
      });
    return () => {
      active = false;
    };
  }, deps);

  return state;
}
