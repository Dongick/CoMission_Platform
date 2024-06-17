import styled from "styled-components";
import Layout from "../../layouts/Layout";
import {
  BannerSection,
  Navbar,
  NavButton,
  MainSection,
  TitleDiv,
} from "./MissionStyles";
import { Link, useParams } from "react-router-dom";
import ConfirmPostList from "../../components/ConfirmPostList";
import { userInfo } from "../../recoil";
import { useRecoilValue } from "recoil";
import { useQuery } from "@tanstack/react-query";
import { getData, postData } from "../../axios";
import { MissionType } from "../../types";
import StyledButton from "../../components/StyledButton";
import example2 from "../../assets/img/no-pictures.png";
import { theme } from "../../styles/theme";
import { useEffect } from "react";
import { AxiosError } from "axios";
import { ErrorResponseDataType } from "../../types";
import { useNavigate } from "react-router-dom";

const MissionConfirmPost = () => {
  const navigate = useNavigate();
  const { cardId } = useParams();
  const detailURL = `/mission/${cardId}/detail`;
  const confirmURL = `/mission/${cardId}/confirm-post`;
  const userInfoState = useRecoilValue(userInfo);
  const fetchData = () => getData<MissionType>(`/api/mission/info/${cardId}`);
  const { data, isLoading, isError, error, refetch } = useQuery({
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
    const axiosError = error as AxiosError<ErrorResponseDataType>;
    const errorCode = axiosError.response?.data.errorCode;
    if (errorCode === "MISSION_NOT_FOUND") {
      return (
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            height: "100%",
          }}
        >
          <h1 style={{ fontSize: "1.5rem", margin: "15px" }}>
            해당 미션이 존재하지 않습니다.
          </h1>
          <StyledButton onClick={() => navigate("/")}>홈으로 이동</StyledButton>
        </div>
      );
    }
  }

  if (!data) {
    return <div>No data available</div>;
  }
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString(); // Format the date as needed
  };
  const participateHandler = async () => {
    try {
      await postData("/api/participant", { id: cardId });
      window.location.reload();
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
          <div style={{ marginBottom: "10px" }}>{data.title}</div>
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
              width: "100%",
            }}
          >
            <p>인증주기: {data.frequency}</p>
            <p>👨‍👧‍👧최소 필요인원: {data.minParticipants}</p>
            <p>👨‍👧‍👧현재 참가인원: {data.participants}</p>
          </div>
          {data.participant ? (
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
                else {
                  participateHandler();
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
          <NavButton clicked="false">🔎 미션 소개</NavButton>
        </Link>
        <Link to={confirmURL}>
          <NavButton clicked="true">📜 미션 인증글</NavButton>
        </Link>
      </Navbar>
      <MainSection>
        {userInfoState.isLoggedIn ? (
          cardId && <ConfirmPostList id={cardId} />
        ) : (
          <NoLoginContent>
            <span>❌</span>
            <h1>미션 멤버만 조회가 가능합니다.</h1>
            <p>로그인 후 미션에 참가해보세요!</p>
          </NoLoginContent>
        )}
      </MainSection>
    </Layout>
  );
};

export default MissionConfirmPost;

export const NoLoginContent = styled.div`
  font-family: "notoBold";
  width: 100%;
  padding-top: 20%;
  & > span,
  h1 {
    font-size: 1.5rem;
    padding: 5%;
  }
  & > p {
    color: grey;
    font-size: 1.2rem;
  }
`;
