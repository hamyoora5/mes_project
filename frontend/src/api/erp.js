import { request } from "./client";

export const erpApi = {
  getResults: () => request("/erp/results")
};
