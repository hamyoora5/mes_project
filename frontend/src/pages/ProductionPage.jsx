import { useState } from "react";
import { workOrdersApi } from "../api/workOrders";
import { PageHeader } from "../components/PageHeader";
import { SectionCard } from "../components/SectionCard";
import { useAsync } from "../hooks/useAsync";
import { formatDateTime } from "../utils/format";

export function ProductionPage() {
  const { data, loading, error } = useAsync(() => workOrdersApi.getAll(), []);
  const [selectedId, setSelectedId] = useState("");
  const { data: history } = useAsync(
    () => (selectedId ? workOrdersApi.getHistory(selectedId) : Promise.resolve([])),
    [selectedId]
  );

  return (
    <div className="page-stack">
      <PageHeader
        eyebrow="Production"
        title="생산 진행 및 이력"
        description="작업지시별 실행 이력과 중단/재가동 구간을 확인합니다."
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
        {loading ? <p>불러오는 중...</p> : null}
        {error ? <p>{error}</p> : null}
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
