import { AppRoutes } from "./routes/AppRoutes.jsx";
import { MainLayout } from "./layouts/MainLayout.jsx";

/**
 * 프론트엔드 앱의 최상위 조립 컴포넌트입니다.
 *
 * 이 파일은 "어떤 페이지를 보여줄지"보다 "앱을 어떤 껍데기 안에서 보여줄지"를
 * 결정하는 곳입니다. 실제 페이지 전환은 AppRoutes가 담당하고, 공통 네비게이션과
 * 화면 프레임은 MainLayout이 담당합니다. 이렇게 분리하면 라우팅 구조를 바꾸더라도
 * 레이아웃을 건드리지 않아도 되고, 반대로 레이아웃을 손보더라도 페이지 정의를
 * 건드리지 않아도 됩니다.
 *
 * @returns {JSX.Element} 공통 레이아웃 안에서 라우트를 렌더링한 앱 루트
 */
export default function App() {
  return (
    <MainLayout>
      <AppRoutes />
    </MainLayout>
  );
}
