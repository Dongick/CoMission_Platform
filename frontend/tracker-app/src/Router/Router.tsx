import { Routes, Route } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
const Router = () => {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      {/* <Route path="/gallery" element={<DetailCardPage />}>
        <Route path=":cardId" element={<DetailCard />} />
      </Route> */}
    </Routes>
  );
};

export default Router;
