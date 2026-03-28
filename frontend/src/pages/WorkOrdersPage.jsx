import { useState } from "react";
import { workOrdersApi } from "../api/workOrders";
import { DataCard } from "../components/DataCard";
import { PageHeader } from "../components/PageHeader";
import { SectionCard } from "../components/SectionCard";
import { useAsync } from "../hooks/useAsync";

const initialForm = {
  workOrderNo: "",
  productCode: "",
  productName: "",
  plannedQty: "",
  lineNo: "",
  equipmentId: "",
  workerId: ""
};

export function WorkOrdersPage() {
  const [refreshKey, setRefreshKey] = useState(0);
  const [form, setForm] = useState(initialForm);
  const [message, setMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const { data, loading, error } = useAsync(() => workOrdersApi.getAll(), [refreshKey]);

  const items = data || [];
  const summary = {
    total: items.length,
    inProgress: items.filter((item) => item.status === "IN_PROGRESS").length,
    completed: items.filter((item) => item.status === "COMPLETED").length
  };

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function handleCreate(event) {
    event.preventDefault();
    setSubmitting(true);
    setMessage("");
    setErrorMessage("");

    try {
      await workOrdersApi.create({
        ...form,
        plannedQty: Number(form.plannedQty)
      });
      setForm(initialForm);
      setMessage("작업지시가 등록되었습니다.");
      setRefreshKey((current) => current + 1);
    } catch (requestError) {
      setErrorMessage(requestError.message);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleStart(id) {
    setMessage("");
    setErrorMessage("");

    try {
      const result = await workOrdersApi.start(id);
      setMessage(`작업이 시작되었습니다. DP 코드: ${result.dailyProdCode}`);
      setRefreshKey((current) => current + 1);
    } catch (requestError) {
      setErrorMessage(requestError.message);
    }
  }

  return (
    <div className="page-stack">
      <PageHeader
        eyebrow="Work Orders"
        title="작업지시 현황"
        description="작업지시 등록과 작업 시작을 여기서 바로 처리합니다."
      />
      <div className="card-grid">
        <DataCard title="전체 작업지시" value={summary.total} />
        <DataCard title="진행 중" value={summary.inProgress} />
        <DataCard title="완료" value={summary.completed} />
      </div>
      <SectionCard title="작업지시 등록">
        <form className="form-grid" onSubmit={handleCreate}>
          <input name="workOrderNo" value={form.workOrderNo} onChange={handleChange} placeholder="작업지시번호" />
          <input name="productCode" value={form.productCode} onChange={handleChange} placeholder="제품코드" />
          <input name="productName" value={form.productName} onChange={handleChange} placeholder="제품명" />
          <input name="plannedQty" value={form.plannedQty} onChange={handleChange} placeholder="계획수량" />
          <input name="lineNo" value={form.lineNo} onChange={handleChange} placeholder="라인번호" />
          <input name="equipmentId" value={form.equipmentId} onChange={handleChange} placeholder="설비 ID" />
          <input name="workerId" value={form.workerId} onChange={handleChange} placeholder="작업자 ID" />
          <button type="submit" disabled={submitting}>
            {submitting ? "등록 중..." : "작업지시 등록"}
          </button>
        </form>
        {message ? <p className="feedback success">{message}</p> : null}
        {errorMessage ? <p className="feedback error">{errorMessage}</p> : null}
      </SectionCard>
      <SectionCard title="작업지시 목록">
        {loading ? <p>불러오는 중...</p> : null}
        {error ? <p>{error}</p> : null}
        {!loading && !error ? (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>작업지시</th>
                  <th>제품</th>
                  <th>계획수량</th>
                  <th>상태</th>
                  <th>DP 코드</th>
                  <th>작업</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.id}>
                    <td>{item.workOrderNo}</td>
                    <td>{item.productName}</td>
                    <td>{item.plannedQty}</td>
                    <td>{item.status}</td>
                    <td>{item.dailyProdCode || "-"}</td>
                    <td>
                      {item.status === "READY" ? (
                        <button type="button" className="table-button" onClick={() => handleStart(item.id)}>
                          작업 시작
                        </button>
                      ) : (
                        <span className="table-hint">진행 중</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : null}
      </SectionCard>
    </div>
  );
}
