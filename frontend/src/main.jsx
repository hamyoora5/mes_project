import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App.jsx";
import "./styles/global.css";

/**
 * React 애플리케이션의 브라우저 진입점입니다.
 *
 * BrowserRouter를 여기서 감싸는 이유는 앱 전체에서 URL 기반 라우팅을 사용하기
 * 위해서입니다. StrictMode는 개발 중 잠재적인 사이드 이펙트를 더 빨리 찾기 위한
 * React 기본 안전장치 역할을 합니다.
 */
ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);
