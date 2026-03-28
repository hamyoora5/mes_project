import { useState } from "react";
import { qualityTestsApi } from "../api/qualityTests";
import { PageHeader } from "../components/PageHeader";
import { SectionCard } from "../components/SectionCard";

export function QualityPage() {
  const [testNo, setTestNo] = useState("");
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");

  async function handleSearch() {
    try {
      setError("");
      setResult(await qualityTestsApi.getByTestNo(testNo));
    } catch (searchError) {
      setResult(null);
      setError(searchError.message);
    }
  }

  return (
    <div className="page-stack">
      <PageHeader
        eyebrow="Quality"
        title="품질 검사 조회"
        description="QT 번호 기준으로 품질 검사 결과와 AI 판정 반영 여부를 조회합니다."
      />
      <SectionCard title="품질번호 조회">
        <div className="search-row">
          <input
            value={testNo}
            onChange={(event) => setTestNo(event.target.value)}
            placeholder="예: QT2026032900ABCD"
          />
          <button type="button" onClick={handleSearch}>
            조회
          </button>
        </div>
        {error ? <p>{error}</p> : null}
      </SectionCard>
      {result ? (
        <SectionCard title="검사 결과">
          <div className="detail-grid">
            <div><span>QT 번호</span><strong>{result.testNo}</strong></div>
            <div><span>DP 코드</span><strong>{result.dailyProdCode}</strong></div>
            <div><span>결과</span><strong>{result.result}</strong></div>
            <div><span>검사 시각</span><strong>{result.testedAt || "-"}</strong></div>
          </div>
        </SectionCard>
      ) : null}
    </div>
  );
}
