import styled from "styled-components";
import Layout from "../layouts/Layout";
import { useParams, useLocation, Link } from "react-router-dom";
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
  font-size: 1.7rem;

  & > div:nth-child(n + 2) {
    font-size: 1rem;
    color: #dee2e6;
    display: flex;
    justify-content: flex-start;
    font-family: "noto";
  }
`;
const Navbar = styled.nav`
  border-bottom: 2px solid #e9ecef;
  height: 5vh;
`;
const NavButton = styled.button`
  height: 100%;
  background-color: red;
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
  const detailURL = `localhost:3000/mission/${cardId}/detail`;
  const confirmURL = `localhost:3000/mission/${cardId}/confirm-post`;
  const missionData = location.state.mission as MissionType;
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
          <div style={{ marginBottom: "30px" }}>
            김영한의 스프링 부트와 JPA 실무 완전 정복 로드맵
          </div>
          <div>
            <p style={{ marginRight: "10px" }}>
              미션 생성일 : {missionData.created.toLocaleDateString()} &nbsp;/
            </p>
            <p>
              미션 진행일 : {missionData.start.toLocaleDateString()} -&nbsp;
              {missionData.deadline.toLocaleDateString()} (
              {missionData.duration}
              일간)
            </p>
          </div>
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              width: "80%",
            }}
          >
            <p>인증주기: {missionData.frequency}</p>
            <p>👨‍👧‍👧최소 필요인원: {missionData.minParticipants}</p>
            <p>👨‍👧‍👧현재 참가인원: {missionData.participants}</p>
          </div>
          <StyledButton
            bgcolor={theme.subGreen}
            style={{
              margin: "20px 0px 0px 0px",
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
      <Navbar>
        <Link to={detailURL}>
          <NavButton>미션 소개</NavButton>
        </Link>
        <Link to={confirmURL}>
          <NavButton>미션 인증글</NavButton>
          {confirmURL}
        </Link>
      </Navbar>
      <MainSection></MainSection>
    </Layout>
  );
};
export default MissionDetail;
