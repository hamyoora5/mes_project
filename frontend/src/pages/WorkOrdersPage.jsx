import { workOrdersApi } from "../api/workOrders";
import { DataCard } from "../components/DataCard";
import { PageHeader } from "../components/PageHeader";
import { SectionCard } from "../components/SectionCard";
import { useAsync } from "../hooks/useAsync";

export function WorkOrdersPage() {
  const { data, loading, error } = useAsync(() => workOrdersApi.getAll(), []);

  const items = data || [];
  const summary = {
    total: items.length,
    inProgress: items.filter((item) => item.status === "IN_PROGRESS").length,
    completed: items.filter((item) => item.status === "COMPLETED").length
  };

  return (
    <div className="page-stack">
      <PageHeader
        eyebrow="Work Orders"
        title="작업지시 현황"
        description="작업지시 목록과 현재 상태를 기준으로 생산 흐름의 시작점을 확인합니다."
      />
      <div className="card-grid">
        <DataCard title="전체 작업지시" value={summary.total} />
        <DataCard title="진행 중" value={summary.inProgress} />
        <DataCard title="완료" value={summary.completed} />
      </div>
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
                </tr>
              </thead>
              <tbody>
                {(data || []).map((item) => (
                  <tr key={item.id}>
                    <td>{item.workOrderNo}</td>
                    <td>{item.productName}</td>
                    <td>{item.plannedQty}</td>
                    <td>{item.status}</td>
                    <td>{item.dailyProdCode || "-"}</td>
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
