import styled from "styled-components";
import Layout from "../../layouts/Layout";
import { useParams, Link } from "react-router-dom";
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
import { useEffect } from "react";

const MissionDetail = () => {
  //todo: cardId -> _id로 변경, title 얻어오는 방식 변경
  const { cardId } = useParams();
  const detailURL = `/mission/${cardId}/detail`;
  const confirmURL = `/mission/${cardId}/confirm-post`;
  const [userInfoState, setUserInfoState] = useRecoilState(userInfo);
  const fetchData = () => getData<MissionType>(`/api/mission/info/${cardId}`);
  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ["missionDetailInfo"],
    queryFn: fetchData,
  });
  useEffect(() => {
    refetch();
  }, [refetch]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    return <div>Error fetching: detail mission data</div>;
  }
  if (!data) {
    return <div>No data available</div>;
  }
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString();
  };
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
          <div style={{ marginBottom: "30px" }}>{data.title}</div>
          <div>
            <p style={{ marginRight: "10px" }}>
              미션 생성일 : {formatDate(data.createdAt)} &nbsp;/
            </p>
            <p>
              ⏱ 미션 진행일 : {formatDate(data.startDate)} -{" "}
              {formatDate(data.deadline)} ({data?.duration} 일간)
            </p>
          </div>
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              width: "80%",
            }}
          >
            <p>인증주기: {data.frequency}</p>
            <p>👨‍👧‍👧최소 필요인원: {data.minParticipants}</p>
            <p>👨‍👧‍👧현재 참가인원: {data.participants}</p>
          </div>
          {!data.participant ? (
            <StyledButton
              bgcolor={theme.subGreen}
              style={{
                margin: "20px 0px 0px 0px",
                fontSize: "large",
                borderRadius: "10px",
                padding: "15px 20px",
                width: "100%",
                backgroundColor: `${theme.subGray}`,
                cursor: "auto",
              }}
            >
              이미 참가한 미션입니다!
            </StyledButton>
          ) : (
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
                if (!userInfoState.isLoggedIn)
                  window.alert("로그인을 해주세요!");
              }}
            >
              미션 참가하기
            </StyledButton>
          )}
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
            {data.username} 님이 만든 미션
          </h1>
          <h2 style={{ paddingTop: "15px", fontSize: "1.2rem" }}>
            {data.status === "CREATED" && (
              <div>
                <span
                  style={{ fontFamily: "notoBold", color: `${theme.subGreen}` }}
                >
                  {(data.minParticipants ?? 0) - (data?.participants ?? 0)}명
                </span>
                이 더 참가시 미션 시작 🚩
              </div>
            )}
            {data.status === "STARTED" && !data.participant && (
              <div>
                <span
                  style={{ fontFamily: "notoBold", color: `${theme.subGreen}` }}
                >
                  미션
                </span>
                에 참가해보세요! 🚩
              </div>
            )}
          </h2>
          <HrDivider />
          <div>
            <div>
              <MissionSubTitle>⚫ 미션 상세 소개</MissionSubTitle>
              <MissionSubContent>{data?.description}</MissionSubContent>
            </div>
          </div>
          <HrDivider />
        </MissionContent>
      </MainSection>
    </Layout>
  );
};
export default MissionDetail;
