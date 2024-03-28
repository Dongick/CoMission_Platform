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
import { getData } from "../../axios";
import { MissionType } from "../../types";
import StyledButton from "../../components/StyledButton";
import example from "../../assets/img/roadmap-77.png";
import { theme } from "../../styles/theme";
const MissionConfirmPost = () => {
  const { cardId } = useParams();
  const detailURL = `/mission/${cardId}/detail`;
  const confirmURL = `/mission/${cardId}/confirm-post`;
  const userInfoState = useRecoilValue(userInfo);
  const fetchData = () => getData<MissionType>(`/api/mission/info/${cardId}`);
  const { data, isLoading, isError } = useQuery({
    queryKey: ["missionDetailInfo"],
    queryFn: fetchData,
  });
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
    return date.toLocaleDateString(); // Format the date as needed
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
  padding-top: 30%;
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
