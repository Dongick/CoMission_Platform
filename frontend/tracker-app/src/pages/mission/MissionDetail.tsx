import styled from "styled-components";
import Layout from "../../layouts/Layout";
import { useParams, useLocation, Link } from "react-router-dom";
import { MissionType } from "../../types";
import { theme } from "../../styles/theme";
import example from "../../assets/img/roadmap-77.png";
import StyledButton from "../../components/StyledButton";
import {
  BannerSection,
  Navbar,
  NavButton,
  MainSection,
  TitleDiv,
} from "./MissionStyles";
const MissionDetail = () => {
  const { cardId } = useParams();
  const location = useLocation();
  const detailURL = `/mission/${cardId}/detail`;
  const confirmURL = `/mission/${cardId}/confirm-post`;
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
          <NavButton clicked={true}>미션 소개</NavButton>
        </Link>
        <Link to={confirmURL}>
          <NavButton clicked={false}>미션 인증글</NavButton>
        </Link>
      </Navbar>
      <MainSection></MainSection>
    </Layout>
  );
};
export default MissionDetail;
