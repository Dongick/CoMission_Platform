import React, { useState } from "react";
import { BrowserRouter } from "react-router-dom";
import Router from "./Router/Router";
import { RecoilRoot } from "recoil";
function App() {
  return (
    <BrowserRouter>
      <RecoilRoot>
        <Router />
      </RecoilRoot>
    </BrowserRouter>
  );
}

export default App;
