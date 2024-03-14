import styled from "styled-components";
import Layout from "../layouts/Layout";
import { useParams } from "react-router-dom";
const MissionDetail = () => {
  const { cardId } = useParams();

  return (
    <Layout>
      미션 디테일 페이지
      <p>Card ID: {cardId}</p>
    </Layout>
  );
};
export default MissionDetail;
