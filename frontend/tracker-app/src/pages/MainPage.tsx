import { theme } from "../styles/theme";
import StyledButton from "../components/StyledButton";
import Layout from "../layouts/Layout";
import styled from "styled-components";
import sectionSVG from "../assets/img/wave-haikei.svg";
import Card from "../components/Card";
import MyCard from "../components/MyCard";
import { userInfo } from "../recoil";
import { useRecoilState } from "recoil";
import { useNavigate } from "react-router";
import Input from "../components/StyledInput";
import { useState, useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
import { MissionType } from "../types";
import axios from "axios";
import { getData } from "../axios";
const cardsData = [
  {
    title: "Title 1",
    author: "Author 1",
    participants: 3,
    description: "이것은 설명입니다",
    minParticipants: 10,
    duration: 365,
    status: "CREATED",
    frequency: "daily",
    creatorEmail: "qkrcksdyd99@gmail.com",
    created: new Date(),
    start: new Date(),
    deadline: new Date(),
  },
  {
    title: "Title 1",
    author: "Author 1",
    participants: 3,
    description: "이것은 설명입니다",
    minParticipants: 10,
    duration: 365,
    status: "CREATED",
    frequency: "daily",
    creatorEmail: "qkrcksdyd99@gmail.com",
    created: new Date(),
    start: new Date(),
    deadline: new Date(),
  },
  {
    title: "Title 1",
    author: "Author 1",
    participants: 3,
    description: "이것은 설명입니다",
    minParticipants: 10,
    duration: 365,
    status: "CREATED",
    frequency: "daily",
    creatorEmail: "qkrcksdyd99@gmail.com",
    created: new Date(),
    start: new Date(),
    deadline: new Date(),
  },
  {
    title: "Title 1",
    author: "Author 1",
    participants: 3,
    description: "이것은 설명입니다",
    minParticipants: 10,
    duration: 365,
    status: "CREATED",
    frequency: "daily",
    creatorEmail: "qkrcksdyd99@gmail.com",
    created: new Date(),
    start: new Date(),
    deadline: new Date(),
  },
  {
    title: "Title 1",
    author: "Author 1",
    participants: 3,
    description: "이것은 설명입니다",
    minParticipants: 10,
    duration: 365,
    status: "CREATED",
    frequency: "daily",
    creatorEmail: "qkrcksdyd99@gmail.com",
    created: new Date(),
    start: new Date(),
    deadline: new Date(),
  },
  {
    title: "Title 1",
    author: "Author 1",
    participants: 3,
    description: "이것은 설명입니다",
    minParticipants: 10,
    duration: 365,
    status: "CREATED",
    frequency: "daily",
    creatorEmail: "qkrcksdyd99@gmail.com",
    created: new Date(),
    start: new Date(),
    deadline: new Date(),
  },
];
const MainPage = () => {
  const navigate = useNavigate();
  const [userInfoState, setUserInfoState] = useRecoilState(userInfo);

  const fetchData = () => getData<MissionType>("/api/main/1");
  const { data, isLoading, isError } = useQuery({
    queryKey: ["missionData"],
    queryFn: fetchData,
  });
  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    return <div>Error fetching data</div>;
  }
  console.log(data);
  return (
    <Layout>
      <SearchSection>
        <div style={{ padding: "10px" }}>미션을 검색해보세요!</div>
        <div
          style={{
            display: "flex",
            flexDirection: "row",
            justifyContent: "center",
            padding: "10px",
          }}
        >
          <Input type="text" placeholder="미션명 검색하기" size={25} />
          <StyledButton
            bgcolor={theme.subGreen}
            color="white"
            style={{ fontSize: "medium" }}
          >
            검색
          </StyledButton>
        </div>
      </SearchSection>
      <StyledButton
        bgcolor={theme.subGreen}
        style={{ margin: "30px", fontSize: "large", borderRadius: "20px" }}
        onClick={() => {
          if (userInfoState.isLoggedIn) window.alert("로그인을 해주세요!");
          else {
            navigate("/mission-create");
          }
        }}
      >
        새로운 미션 등록
      </StyledButton>
      {userInfoState.isLoggedIn && (
        <div
          style={{
            backgroundColor: "#F5F5F5",
          }}
        >
          <h2
            style={{
              fontSize: "1.3rem",
              fontFamily: "gmarket2",
              paddingTop: "20px",
              width: "40%",
              margin: "0 auto",
            }}
          >
            내가 참가한 미션
            <span style={{ color: `${theme.subGreen}`, paddingLeft: "10px" }}>
              {cardsData.length}
            </span>
          </h2>
          <MyMissionSection>
            {cardsData.map((card, index) => (
              <MyCard
                key={index}
                id={index + 1}
                title={card.title}
                start={card.start}
                deadline={card.deadline}
                people={card.participants}
              />
            ))}
          </MyMissionSection>
        </div>
      )}
      <MainSection>
        {cardsData.map((card, index) => (
          <Card
            key={index}
            id={index + 1}
            title={card.title}
            author={card.author}
            people={card.participants}
            missionData={card}
          />
        ))}
      </MainSection>
    </Layout>
  );
};

export default MainPage;

export const SearchSection = styled.section`
  background-image: url(${sectionSVG});
  background-size: cover;
  background-position: center;
  height: 20vh;
  padding: 10px;
  font-family: "gmarket2";
  font-size: 2rem;
  color: #333;
  display: flex;
  flex-direction: column;
  justify-content: center;
`;

const MainSection = styled.section`
  min-height: 100vh;
  padding: 3vh;
  width: 70%;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(15vw, 1fr));
  gap: 20px; /* Adjust the gap between cards */
`;

const MyMissionSection = styled.section`
  padding: 10px;
  margin: 0 auto;
  margin-bottom: 5vh;
  height: 30vh;
  width: 50%;
  display: flex;
  overflow-x: auto;
  align-items: center;
  &::-webkit-scrollbar {
    width: 100%;
  }
  &::-webkit-scrollbar-thumb {
    background-color: ${theme.subGray2};
    border-radius: 10px;
    background-clip: padding-box;
    border: 3px solid transparent;
  }
  &::-webkit-scrollbar-track {
    background-color: ${theme.subGray};
    border-radius: 10px;
    box-shadow: inset 0px 0px 2px white;
  }
`;
