import { Routes, Route } from "react-router-dom";
import MainPage from "../pages/MainPage";
import MissionDetail from "../pages/mission/MissionDetail";
import MissionConfirmPost from "../pages/mission/MissionConfirmPost";
const Router = () => {
  return (
    <Routes>
      <Route path="/" element={<MainPage />} />
      <Route path="/mission/:cardId/detail" element={<MissionDetail />} />
      <Route
        path="/mission/:cardId/confirm-post"
        element={<MissionConfirmPost />}
      />
    </Routes>
  );
};

export default Router;
