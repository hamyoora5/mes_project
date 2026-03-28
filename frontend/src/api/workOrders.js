import { request } from "./client";

export const workOrdersApi = {
  getAll: () => request("/work-orders"),
  create: (payload) =>
    request("/work-orders", {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  start: (id) => request(`/work-orders/${id}/start`, { method: "POST" }),
  recordProduction: (id, payload) =>
    request(`/work-orders/${id}/production`, {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  registerDefect: (id, payload) =>
    request(`/work-orders/${id}/defects`, {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  stop: (id, payload) =>
    request(`/work-orders/${id}/stop`, {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  resume: (id) => request(`/work-orders/${id}/resume`, { method: "POST" }),
  complete: (id) => request(`/work-orders/${id}/complete`, { method: "POST" }),
  getHistory: (id) => request(`/work-orders/${id}/history`)
};
