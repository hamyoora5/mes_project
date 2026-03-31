import { erpApi } from "../api/erp";
import { PageHeader } from "../components/PageHeader";
import { SectionCard } from "../components/SectionCard";
import { useAsync } from "../hooks/useAsync";
import { formatDateTime } from "../utils/format";

/**
 * ERP 전송 결과 화면입니다.
 *
 * 생산 종료 후 집계된 결과를 ERP 관점에서 검토하기 위한 조회 페이지입니다.
 * 실제 ERP 전송 기능을 아직 구현하지 않았더라도, 어떤 데이터가 전송 대상인지
 * 먼저 확인할 수 있게 해 주는 역할을 합니다.
 *
 * @returns {JSX.Element} ERP 결과 조회 화면
 */
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
