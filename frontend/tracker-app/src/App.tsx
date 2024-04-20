import React from "react";
import { BrowserRouter } from "react-router-dom";
import ScrollToTop from "./components/ScrollToTop";
import Router from "./Router/Router";
import { RecoilRoot } from "recoil";
function App() {
  return (
    <BrowserRouter>
      <ScrollToTop />
      <RecoilRoot>
        <Router />
      </RecoilRoot>
    </BrowserRouter>
  );
}

export default App;
