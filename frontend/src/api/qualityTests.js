import { request } from "./client";

export const qualityTestsApi = {
  create: (payload) =>
    request("/quality-tests", {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  applyAiResult: (testNo, payload) =>
    request(`/quality-tests/${testNo}/ai-result`, {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  getByTestNo: (testNo) => request(`/quality-tests/${testNo}`)
};
