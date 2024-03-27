import { Routes, Route } from "react-router-dom";
import MainPage from "../pages/MainPage";
import MissionDetailPage from "../pages/mission/MissionDetailPage";
import MissionConfirmPostPage from "../pages/mission/MissionConfirmPostPage";
import MissionCreatePage from "../pages/mission-manage/MissionCreatePage";
const Router = () => {
  return (
    <Routes>
      <Route path="/" element={<MainPage />} />
      <Route path="/mission/:cardId/detail" element={<MissionDetailPage />} />
      <Route
        path="/mission/:cardId/confirm-post"
        element={<MissionConfirmPostPage />}
      />
      <Route path="/mission-create" element={<MissionCreatePage />} />
    </Routes>
  );
};

export default Router;
