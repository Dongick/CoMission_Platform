import styled from "styled-components";
import Layout from "../../layouts/Layout";
import { useParams, useLocation, Link } from "react-router-dom";
import { theme } from "../../styles/theme";
import example from "../../assets/img/roadmap-77.png";
import StyledButton from "../../components/StyledButton";
import {
  BannerSection,
  Navbar,
  NavButton,
  MainSection,
  TitleDiv,
  MissionContent,
  MissionSubTitle,
  MissionSubContent,
  HrDivider,
} from "./MissionStyles";
import { userInfo } from "../../recoil";
import { useRecoilState } from "recoil";
import { useQuery } from "@tanstack/react-query";
import { MissionType } from "../../types";
import { getData } from "../../axios";

const MissionDetail = () => {
  const { cardId } = useParams();
  const location = useLocation();
  const detailURL = `/mission/${cardId}/detail`;
  const confirmURL = `/mission/${cardId}/confirm-post`;
  const title = location.state.title;
  const [userInfoState, setUserInfoState] = useRecoilState(userInfo);
  const fetchData = () => getData<MissionType>(`/api/mission/info/${title}`);
  const { data, isLoading, isError } = useQuery({
    queryKey: ["missionDetailInfo"],
    queryFn: fetchData,
  });
  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    return <div>Error fetching detail mission data</div>;
  }
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
              미션 생성일 : {data?.created} &nbsp;/
            </p>
            <p>
              ⏱ 미션 진행일 : {data?.start} -{data?.deadline}({data?.duration}
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
            <p>인증주기: {data?.frequency}</p>
            <p>👨‍👧‍👧최소 필요인원: {data?.minParticipants}</p>
            <p>👨‍👧‍👧현재 참가인원: {data?.participants}</p>
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
            onClick={() => {
              if (!userInfoState.isLoggedIn) window.alert("로그인을 해주세요!");
            }}
          >
            미션 참가하기
          </StyledButton>
        </TitleDiv>
      </BannerSection>
      <Navbar>
        <Link to={detailURL}>
          <NavButton clicked="true">🔎 미션 소개</NavButton>
        </Link>
        <Link to={confirmURL}>
          <NavButton clicked="false">📜 미션 인증글</NavButton>
        </Link>
      </Navbar>
      <MainSection>
        <MissionContent>
          <h1
            style={{
              fontFamily: "gmarket2",
              fontSize: "1.3rem",
              paddingTop: "20px",
            }}
          >
            {data?.creatorEmail} 님이 만든 미션
          </h1>
          <h2 style={{ paddingTop: "15px", fontSize: "1.2rem" }}>
            <span
              style={{ fontFamily: "notoBold", color: `${theme.subGreen}` }}
            >
              {(data?.minParticipants ?? 0) - (data?.participants ?? 0)}명
            </span>
            이 더 참가시 미션 시작 🚩
          </h2>
          <HrDivider />
          <div>
            <div>
              <MissionSubTitle>⚫ 미션 상세 소개</MissionSubTitle>
              <MissionSubContent>미션에 대한 내용</MissionSubContent>
            </div>
            <HrDivider />
            <div>
              <MissionSubTitle>⚫ 이렇게 인증해 주세요!</MissionSubTitle>
              <MissionSubContent>미션 인증 규칙에 대한 내용</MissionSubContent>
            </div>
          </div>
          <HrDivider />
        </MissionContent>
      </MainSection>
    </Layout>
  );
};
export default MissionDetail;
