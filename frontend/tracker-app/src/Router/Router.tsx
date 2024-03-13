import { Routes, Route } from "react-router-dom";
import MainPage from "../pages/MainPage";
const Router = () => {
  return (
    <Routes>
      <Route path="/" element={<MainPage />} />
      {/* <Route path="/gallery" element={<DetailCardPage />}>
        <Route path=":cardId" element={<DetailCard />} />
      </Route> */}
    </Routes>
  );
};

export default Router;
