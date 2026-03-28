import { useEffect, useState } from "react";
import { workOrdersApi } from "../api/workOrders";
import { PageHeader } from "../components/PageHeader";
import { SectionCard } from "../components/SectionCard";
import { useAsync } from "../hooks/useAsync";
import { formatDateTime } from "../utils/format";

const initialProductionForm = { producedQty: "" };
const initialDefectForm = { defectType: "", defectQty: "" };
const initialStopForm = { stopReason: "" };

export function ProductionPage() {
  const [refreshKey, setRefreshKey] = useState(0);
  const [selectedId, setSelectedId] = useState("");
  const [productionForm, setProductionForm] = useState(initialProductionForm);
  const [defectForm, setDefectForm] = useState(initialDefectForm);
  const [stopForm, setStopForm] = useState(initialStopForm);
  const [message, setMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const { data, loading, error } = useAsync(() => workOrdersApi.getAll(), [refreshKey]);
  const { data: history } = useAsync(
    () => (selectedId ? workOrdersApi.getHistory(selectedId) : Promise.resolve([])),
    [selectedId, refreshKey]
  );

  useEffect(() => {
    if (!selectedId && data?.length) {
      const firstActive = data.find((item) => item.status !== "COMPLETED");
      if (firstActive) {
        setSelectedId(String(firstActive.id));
      }
    }
  }, [data, selectedId]);

  const selectedWorkOrder = (data || []).find((item) => String(item.id) === selectedId);

  function bumpRefresh() {
    setRefreshKey((current) => current + 1);
  }

  function resetMessages() {
    setMessage("");
    setErrorMessage("");
  }

  async function execute(action) {
    resetMessages();

    try {
      const result = await action();
      setMessage(result?.dailyProdCode ? `처리 완료: ${result.dailyProdCode}` : "처리가 완료되었습니다.");
      bumpRefresh();
    } catch (requestError) {
      setErrorMessage(requestError.message);
    }
  }

  return (
    <div className="page-stack">
      <PageHeader
        eyebrow="Production"
        title="생산 진행 및 이력"
        description="생산량 입력, 불량 등록, 중단/재가동/종료를 한 화면에서 처리합니다."
      />
      <SectionCard title="작업 선택">
        <select value={selectedId} onChange={(event) => setSelectedId(event.target.value)}>
          <option value="">작업지시를 선택하세요</option>
          {(data || []).map((item) => (
            <option key={item.id} value={item.id}>
              {item.workOrderNo} / {item.status}
            </option>
          ))}
        </select>
        {selectedWorkOrder ? (
          <div className="detail-grid compact">
            <div><span>상태</span><strong>{selectedWorkOrder.status}</strong></div>
            <div><span>DP 코드</span><strong>{selectedWorkOrder.dailyProdCode || "-"}</strong></div>
          </div>
        ) : null}
        {loading ? <p>불러오는 중...</p> : null}
        {error ? <p>{error}</p> : null}
        {message ? <p className="feedback success">{message}</p> : null}
        {errorMessage ? <p className="feedback error">{errorMessage}</p> : null}
      </SectionCard>

      <div className="action-grid">
        <SectionCard title="생산량 입력">
          <form
            className="inline-form"
            onSubmit={(event) => {
              event.preventDefault();
              execute(() =>
                workOrdersApi.recordProduction(selectedId, {
                  producedQty: Number(productionForm.producedQty)
                })
              );
              setProductionForm(initialProductionForm);
            }}
          >
            <input
              value={productionForm.producedQty}
              onChange={(event) => setProductionForm({ producedQty: event.target.value })}
              placeholder="생산 수량"
            />
            <button type="submit" disabled={!selectedId}>생산 입력</button>
          </form>
        </SectionCard>

        <SectionCard title="불량 등록">
          <form
            className="inline-form two-line"
            onSubmit={(event) => {
              event.preventDefault();
              execute(() =>
                workOrdersApi.registerDefect(selectedId, {
                  defectType: defectForm.defectType,
                  defectQty: Number(defectForm.defectQty)
                })
              );
              setDefectForm(initialDefectForm);
            }}
          >
            <input
              value={defectForm.defectType}
              onChange={(event) => setDefectForm((current) => ({ ...current, defectType: event.target.value }))}
              placeholder="불량 유형"
            />
            <input
              value={defectForm.defectQty}
              onChange={(event) => setDefectForm((current) => ({ ...current, defectQty: event.target.value }))}
              placeholder="불량 수량"
            />
            <button type="submit" disabled={!selectedId}>불량 등록</button>
          </form>
        </SectionCard>
      </div>

      <SectionCard title="상태 제어">
        <div className="control-group">
          <form
            className="inline-form"
            onSubmit={(event) => {
              event.preventDefault();
              execute(() => workOrdersApi.stop(selectedId, stopForm));
              setStopForm(initialStopForm);
            }}
          >
            <input
              value={stopForm.stopReason}
              onChange={(event) => setStopForm({ stopReason: event.target.value })}
              placeholder="중단 사유"
            />
            <button type="submit" disabled={!selectedId}>작업 중단</button>
          </form>
          <div className="button-row">
            <button type="button" onClick={() => execute(() => workOrdersApi.resume(selectedId))} disabled={!selectedId}>
              재가동
            </button>
            <button type="button" onClick={() => execute(() => workOrdersApi.complete(selectedId))} disabled={!selectedId}>
              작업 종료
            </button>
          </div>
        </div>
      </SectionCard>

      <SectionCard title="생산 이력">
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Run</th>
                <th>상태</th>
                <th>시작</th>
                <th>종료</th>
                <th>생산</th>
                <th>불량</th>
                <th>중단사유</th>
              </tr>
            </thead>
            <tbody>
              {(history || []).map((item) => (
                <tr key={item.id}>
                  <td>{item.runSeq}</td>
                  <td>{item.status}</td>
                  <td>{formatDateTime(item.startTime)}</td>
                  <td>{formatDateTime(item.endTime)}</td>
                  <td>{item.producedQty}</td>
                  <td>{item.defectQty}</td>
                  <td>{item.stopReason || "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </SectionCard>
    </div>
  );
}
