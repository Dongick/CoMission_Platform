import styled from "styled-components";
import Layout from "../layouts/Layout";
import { useParams, useLocation } from "react-router-dom";
import { MissionType } from "../types";

const MissionDetail = () => {
  const { cardId } = useParams();
  const location = useLocation();
  const missionData = location.state.mission as MissionType;
  console.log(missionData);
  return (
    <Layout>
      미션 디테일 페이지
      <p>Card ID: {cardId}</p>
    </Layout>
  );
};
export default MissionDetail;
