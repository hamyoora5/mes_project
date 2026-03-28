import { erpApi } from "../api/erp";
import { PageHeader } from "../components/PageHeader";
import { SectionCard } from "../components/SectionCard";
import { useAsync } from "../hooks/useAsync";
import { formatDateTime } from "../utils/format";

export function ErpResultsPage() {
  const { data, loading, error } = useAsync(() => erpApi.getResults(), []);

  return (
    <div className="page-stack">
      <PageHeader
        eyebrow="ERP"
        title="ERP 전송 결과"
        description="생산 종료 후 집계된 결과를 ERP 전달 관점에서 확인합니다."
      />
      <SectionCard title="전송 대상 목록">
        {loading ? <p>불러오는 중...</p> : null}
        {error ? <p>{error}</p> : null}
        {!loading && !error ? (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>작업지시</th>
                  <th>제품코드</th>
                  <th>DP 코드</th>
                  <th>총수량</th>
                  <th>양품</th>
                  <th>불량</th>
                  <th>상태</th>
                  <th>완료시각</th>
                </tr>
              </thead>
              <tbody>
                {(data || []).map((item) => (
                  <tr key={item.dailyProdCode}>
                    <td>{item.workOrderNo}</td>
                    <td>{item.productCode}</td>
                    <td>{item.dailyProdCode}</td>
                    <td>{item.totalQty}</td>
                    <td>{item.goodQty}</td>
                    <td>{item.defectQty}</td>
                    <td>{item.status}</td>
                    <td>{formatDateTime(item.completedAt)}</td>
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
