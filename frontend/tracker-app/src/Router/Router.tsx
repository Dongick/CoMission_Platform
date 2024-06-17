import { Routes, Route } from "react-router-dom";
import MainPage from "../pages/MainPage";
import MissionDetailPage from "../pages/mission/MissionDetailPage";
import MissionConfirmPostPage from "../pages/mission/MissionConfirmPostPage";
import MissionCreatePage from "../pages/mission-manage/MissionCreatePage";
import MissionEditPage from "../pages/mission-manage/MissionEditPage";
import MyPage from "../pages/MyPage";
const Router = () => {
  return (
    <Routes>
      <Route path="/" element={<MainPage />} />
      <Route path="/my-page" element={<MyPage />} />
      <Route path="/mission/:cardId/detail" element={<MissionDetailPage />} />
      <Route
        path="/mission/:cardId/confirm-post"
        element={<MissionConfirmPostPage />}
      />
      <Route path="/mission-create" element={<MissionCreatePage />} />
      <Route path="/mission-edit/:cardId" element={<MissionEditPage />} />
    </Routes>
  );
};

export default Router;
