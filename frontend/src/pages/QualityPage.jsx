import { useState } from "react";
import { qualityTestsApi } from "../api/qualityTests";
import { PageHeader } from "../components/PageHeader";
import { SectionCard } from "../components/SectionCard";
import { formatDateTime } from "../utils/format";

const initialCreateForm = {
  dailyProdCode: "",
  imagePath: ""
};

const initialAiForm = {
  testNo: "",
  result: "PASS"
};

/**
 * 품질 검사 등록과 AI 결과 반영을 담당하는 화면입니다.
 *
 * 이 페이지는 MES에서 품질 흐름을 검증하기 위한 핵심 화면입니다.
 * - DP 코드로 품질 검사 생성
 * - QT 번호로 AI 결과 반영
 * - QT 번호로 검사 결과 조회
 *
 * 현재 프로젝트는 실제 AI 추론 서버 구현보다 "판정 결과를 MES 상태 흐름에 반영하는
 * 과정"이 중요하므로, 이 페이지는 그 연결을 시연하는 역할을 합니다.
 *
 * @returns {JSX.Element} 품질 등록/반영/조회 화면
 */
export function QualityPage() {
  const [createForm, setCreateForm] = useState(initialCreateForm);
  const [aiForm, setAiForm] = useState(initialAiForm);
  const [searchTestNo, setSearchTestNo] = useState("");
  const [result, setResult] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  function resetFeedback() {
    setMessage("");
    setError("");
  }

  /**
   * 새로운 품질 검사 대상을 생성합니다.
   *
   * @param {React.FormEvent<HTMLFormElement>} event 폼 제출 이벤트
   * @returns {Promise<void>}
   */
  async function handleCreate(event) {
    event.preventDefault();
    resetFeedback();

    try {
      const created = await qualityTestsApi.create(createForm);
      setResult(created);
      setSearchTestNo(created.testNo);
      setAiForm((current) => ({ ...current, testNo: created.testNo }));
      setCreateForm(initialCreateForm);
      setMessage(`품질 검사가 등록되었습니다. QT 번호: ${created.testNo}`);
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  /**
   * 특정 QT에 AI 판정 결과를 반영합니다.
   *
   * @param {React.FormEvent<HTMLFormElement>} event 폼 제출 이벤트
   * @returns {Promise<void>}
   */
  async function handleApplyAi(event) {
    event.preventDefault();
    resetFeedback();

    try {
      const updated = await qualityTestsApi.applyAiResult(aiForm.testNo, {
        result: aiForm.result
      });
      setResult(updated);
      setSearchTestNo(updated.testNo);
      setMessage(`AI 판정 결과가 반영되었습니다. 결과: ${updated.result}`);
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  /**
   * QT 번호 기준으로 품질 검사 결과를 조회합니다.
   *
   * @returns {Promise<void>}
   */
  async function handleSearch() {
    resetFeedback();

    try {
      setResult(await qualityTestsApi.getByTestNo(searchTestNo));
    } catch (requestError) {
      setResult(null);
      setError(requestError.message);
    }
  }

  return (
    <div className="page-stack">
      <PageHeader
        eyebrow="Quality"
        title="품질 검사 및 AI 판정"
        description="DP 기준 품질 등록, QT 기준 AI 결과 반영, 품질 결과 조회를 처리합니다."
      />

      <div className="action-grid">
        <SectionCard title="품질 검사 등록">
          <form className="inline-form two-line" onSubmit={handleCreate}>
            <input
              value={createForm.dailyProdCode}
              onChange={(event) => setCreateForm((current) => ({ ...current, dailyProdCode: event.target.value }))}
              placeholder="DP 코드"
            />
            <input
              value={createForm.imagePath}
              onChange={(event) => setCreateForm((current) => ({ ...current, imagePath: event.target.value }))}
              placeholder="이미지 경로"
            />
            <button type="submit">품질 등록</button>
          </form>
        </SectionCard>

        <SectionCard title="AI 결과 반영">
          <form className="inline-form two-line" onSubmit={handleApplyAi}>
            <input
              value={aiForm.testNo}
              onChange={(event) => setAiForm((current) => ({ ...current, testNo: event.target.value }))}
              placeholder="QT 번호"
            />
            <select
              value={aiForm.result}
              onChange={(event) => setAiForm((current) => ({ ...current, result: event.target.value }))}
            >
              <option value="PASS">PASS</option>
              <option value="FAIL">FAIL</option>
            </select>
            <button type="submit">AI 반영</button>
          </form>
        </SectionCard>
      </div>

      <SectionCard title="품질번호 조회">
        <div className="search-row">
          <input
            value={searchTestNo}
            onChange={(event) => setSearchTestNo(event.target.value)}
            placeholder="예: QT20260329XXXXXX"
          />
          <button type="button" onClick={handleSearch}>
            조회
          </button>
        </div>
        {message ? <p className="feedback success">{message}</p> : null}
        {error ? <p className="feedback error">{error}</p> : null}
      </SectionCard>

      {result ? (
        <SectionCard title="검사 결과">
          <div className="detail-grid">
            <div><span>QT 번호</span><strong>{result.testNo}</strong></div>
            <div><span>DP 코드</span><strong>{result.dailyProdCode}</strong></div>
            <div><span>결과</span><strong>{result.result}</strong></div>
            <div><span>검사 시각</span><strong>{formatDateTime(result.testedAt)}</strong></div>
            <div><span>이미지 경로</span><strong>{result.imagePath || "-"}</strong></div>
          </div>
        </SectionCard>
      ) : null}
    </div>
  );
}
