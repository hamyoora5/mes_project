import { Navigate, Route, Routes } from "react-router-dom";
import { WorkOrdersPage } from "../pages/WorkOrdersPage.jsx";
import { ProductionPage } from "../pages/ProductionPage.jsx";
import { QualityPage } from "../pages/QualityPage.jsx";
import { ErpResultsPage } from "../pages/ErpResultsPage.jsx";

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
