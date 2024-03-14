import { Routes, Route } from "react-router-dom";
import MainPage from "../pages/MainPage";
import MissionDetail from "../pages/MissionDetail";
const Router = () => {
  return (
    <Routes>
      <Route path="/" element={<MainPage />} />
      <Route path="/mission/:cardId" element={<MissionDetail />} />
    </Routes>
  );
};

export default Router;
