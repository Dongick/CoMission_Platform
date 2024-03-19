import styled from "styled-components";
import Layout from "../layouts/Layout";
import { useParams, useLocation } from "react-router-dom";
import { MissionType } from "../types";
import { SearchSection } from "./MainPage";
import { theme } from "../styles/theme";
import example from "../assets/img/roadmap-77.png";
import StyledButton from "../components/StyledButton";

const BannerSection = styled(SearchSection)`
  background-color: #25262b;
  background-image: none;
  color: white;
  height: 30vh;
  flex-direction: row;
  align-items: center;
  margin-bottom: 0;
`;
const TitleDiv = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-start;
  height: 70%;
  font-size: 1.6rem;

  & > div:nth-child(n + 2) {
    font-size: 1rem;
    color: #dee2e6;
    display: flex;
    justify-content: flex-start;
    & > p {
      /* padding: 5px; */
    }
  }
`;
const Navbar = styled.nav`
  border-bottom: 3px solid ${theme.mainGray};
  height: 5vh;
`;
const MainSection = styled.section`
  min-height: 100vh;
  padding: 3vh;
  width: 50%;
  margin: 0 auto;
  background-color: ${theme.mainGray};
`;
const MissionDetail = () => {
  const { cardId } = useParams();
  const location = useLocation();
  const missionData = location.state.mission as MissionType;
  console.log(missionData.start);
  return (
    <Layout>
      <BannerSection>
        <img
          src={example}
          alt="img"
          style={{
            width: "20%",
            height: "90%",
            marginRight: "30px",
            borderRadius: "10px",
          }}
        />
        <TitleDiv>
          <div>김영한의 스프링 부트와 JPA 실무 완전 정복 로드맵</div>
          <div>
            <p>{missionData.start.toLocaleString()}</p>
            <p>{missionData.created.toLocaleString()}</p>
            <p>{missionData.deadline.toLocaleString()}</p>
          </div>
          <div>
            <p>{missionData.duration}</p>
            <p>{missionData.frequency}</p>
            <p>{missionData.minParticipants}</p>
            <p>{missionData.participants}</p>
          </div>
          <StyledButton
            bgcolor={theme.subGreen}
            style={{
              margin: "0px",
              fontSize: "large",
              borderRadius: "10px",
              padding: "15px 20px",
              width: "100%",
            }}
          >
            미션 참가하기
          </StyledButton>
        </TitleDiv>
      </BannerSection>
      <Navbar>가나다라</Navbar>
      <MainSection></MainSection>
    </Layout>
  );
};
export default MissionDetail;
