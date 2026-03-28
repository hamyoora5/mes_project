const API_BASE_URL = "http://localhost:8002";

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
