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
import { getData, postData } from "../../axios";
import { useEffect } from "react";
import example2 from "../../assets/img/no-pictures.png";

const MissionDetail = () => {
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
  }, [data]);

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
  const partipateHandler = async () => {
    try {
      const data = await postData("/api/participant", { id: cardId });
      console.log(data);
    } catch (error) {
      console.error(error);
    }
  };

  const isStartedDate = (
    <p>
      ⏱ 미션 진행일 : {formatDate(data.startDate)} - {formatDate(data.deadline)}{" "}
      ({data?.duration} 일간)
    </p>
  );
  return (
    <Layout>
      <BannerSection>
        {data.photoUrl ? (
          <img
            src={data.photoUrl}
            alt="img"
            style={{
              width: "20%",
              height: "90%",
              marginRight: "30px",
              borderRadius: "10px",
            }}
          />
        ) : (
          <img
            src={example2}
            alt="img"
            style={{
              width: "15%",
              height: "80%",
              marginRight: "30px",
              borderRadius: "10px",
            }}
          />
        )}
        <TitleDiv>
          <div style={{ marginBottom: "30px" }}>{data.title}</div>
          <div>
            <p style={{ marginRight: "10px" }}>
              미션 생성일 : {formatDate(data.createdAt)} &nbsp;/
            </p>
            {data.status === "CREATED" ? (
              <p>⏱ 멤버모집이 완료되어야 시작됩니다!</p>
            ) : (
              isStartedDate
            )}
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
          {data.participant ? (
            <StyledButton
              bgcolor={theme.subGray}
              style={{
                margin: "20px 0px 0px 0px",
                fontSize: "large",
                borderRadius: "10px",
                padding: "15px 20px",
                width: "100%",
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
                else {
                  partipateHandler();
                }
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
