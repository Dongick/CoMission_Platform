import Layout from "../../layouts/Layout";
import styled from "styled-components";
import { SearchSection } from "../MainPage";
import { theme } from "../../styles/theme";
import missionImg from "../../assets/img/mission-img.png";
const MissionCreatePage = () => {
  return (
    <Layout footer={false}>
      <div style={{ backgroundColor: `${theme.mainGray}` }}>
        <MissionCreateBanner>
          <div>
            <h2>미션 공유 플랫폼이란?</h2>
            <p>
              미션 실천내용을 인증하며 같은 목표를 가지고 진행도를 공유합니다.
              <br />
              인증을 함께 할 최소 인원을 정하고, 멤버 모집 후 개설이 됩니다.
            </p>
          </div>
          <img src={missionImg} alt="zz" width={100} height={100} />
        </MissionCreateBanner>
        <MissionFormWrapper>
          <MissionFormView></MissionFormView>
        </MissionFormWrapper>
      </div>
    </Layout>
  );
};

export default MissionCreatePage;

const MissionCreateBanner = styled(SearchSection)`
  background: none;
  background-color: white;
  border: 2px solid ${theme.mainGray};
  line-height: 2rem;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  & > div > h2 {
    font-family: "gmarket2";
    font-size: 1.5rem;
    text-align: left;
    padding: 5px;
  }
  & > div > p {
    font-family: "gmarket1";
    font-size: 1.2rem;
    text-align: left;
    padding: 5px;
  }
`;

const MissionFormWrapper = styled.section`
  min-height: 100vh;
  padding: 3vh;
  width: 50%;
  margin: 0 auto;
`;

const MissionFormView = styled.div`
  width: 100%;
  min-height: 600px;
  background-color: white;
`;
