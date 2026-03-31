import { Navigate, Route, Routes } from "react-router-dom";
import { WorkOrdersPage } from "../pages/WorkOrdersPage.jsx";
import { ProductionPage } from "../pages/ProductionPage.jsx";
import { QualityPage } from "../pages/QualityPage.jsx";
import { ErpResultsPage } from "../pages/ErpResultsPage.jsx";

/**
 * 프론트엔드 라우팅 정의입니다.
 *
 * 현재 기능이 작업지시, 생산, 품질, ERP 네 영역으로 나뉘므로 각각을 별도 경로로
 * 분리했습니다. 기본 경로(`/`)는 작업지시 화면으로 보내서 사용자가 흐름의 시작점부터
 * 진입하도록 유도합니다.
 *
 * @returns {JSX.Element} 라우트 정의
 */
export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/work-orders" replace />} />
      <Route path="/work-orders" element={<WorkOrdersPage />} />
      <Route path="/production" element={<ProductionPage />} />
      <Route path="/quality" element={<QualityPage />} />
      <Route path="/erp" element={<ErpResultsPage />} />
    </Routes>
  );
}
