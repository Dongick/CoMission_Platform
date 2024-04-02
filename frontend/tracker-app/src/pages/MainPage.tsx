import { theme } from "../styles/theme";
import StyledButton from "../components/StyledButton";
import Layout from "../layouts/Layout";
import styled from "styled-components";
import Card from "../components/Card";
import MyCard from "../components/MyCard";
import { userInfo } from "../recoil";
import { useRecoilState } from "recoil";
import { useNavigate, useLocation } from "react-router";
import { useQuery, useInfiniteQuery } from "@tanstack/react-query";
import {
  MainServerResponseType,
  SearchedMissionInfoType,
  SimpleMissionInfoType,
  LazyMissionInfoListType,
} from "../types";
import { getData, postData } from "../axios";
import { useEffect, useState } from "react";
import useLogout from "../useLogout";
import MissionSearch from "../components/MissionSearch";
import { NoLoginContent } from "./mission/MissionConfirmPostPage";

const MainPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const logout = useLogout();
  const [userInfoState, setUserInfoState] = useRecoilState(userInfo);
  const [totalMissionData, setTotalMissionData] = useState<
    SimpleMissionInfoType[]
  >([]);
  const [myMissionData, setMyMissionData] = useState<SimpleMissionInfoType[]>(
    []
  );
  const [noDataMessage, setNoDataMessage] =
    useState<string>("생성된 미션이 없습니다!");
  useEffect(() => {
    const urlSearchParams = new URLSearchParams(location.search);
    const accessToken = urlSearchParams.get("AccessToken");
    const email = urlSearchParams.get("email");
    const name = urlSearchParams.get("username");
    if (accessToken) {
      localStorage.setItem("accessToken", accessToken);
      setUserInfoState({
        isLoggedIn: true,
        user_id: `${name}`,
        user_email: `${email}`,
      });
      navigate("/");
    }
  }, [location.search, setUserInfoState, navigate]);

  const [currentNum, setCurrentNum] = useState<number>(1);
  const fetchLazyData = async ({ pageParam = 1 }) =>
    await getData<LazyMissionInfoListType>(`/api/main/${pageParam}`);
  const {
    data: lazyData,
    hasNextPage,
    fetchNextPage,
    isFetchingNextPage,
  } = useInfiniteQuery({
    queryKey: ["lazyMissionData"],
    queryFn: fetchLazyData,
    initialPageParam: 1,
    getNextPageParam: (lastList, allLists) => {
      console.log(lastList);
      console.log(allLists);
      if (lastList.missionInfoList.length < 20) {
        return undefined;
      }
      setCurrentNum((prev) => prev + 1);
      return currentNum;
    },
    select: (data) => data.pages.flatMap((page) => page.missionInfoList),
  });

  const fetchData = async () =>
    await getData<MainServerResponseType>("/api/main");
  const { data, isLoading, isError, isSuccess } = useQuery({
    queryKey: ["totalMissionData"],
    queryFn: fetchData,
  });

  useEffect(() => {
    if (isSuccess || data) {
      setTotalMissionData(data.missionInfoList);
      setMyMissionData(data.participantMissionInfoList);
    }
  }, [isSuccess, data]);

  if (isError) {
    if (userInfoState.isLoggedIn) logout();
  }

  const updateData = (newData: SearchedMissionInfoType) => {
    console.log(newData.missionInfoList.length);
    if (newData.missionInfoList.length === 0) {
      setNoDataMessage("검색결과가 없습니다!");
    } else {
      setNoDataMessage("생성된 미션이 없습니다!");
    }
    setTotalMissionData(newData.missionInfoList);
  };

  return (
    <Layout>
      <MissionSearch updateData={updateData} />
      {isLoading && <p>Loading...</p>}
      {isError && <p>Data Fetching Error</p>}
      <StyledButton
        bgcolor={theme.subGreen}
        style={{ margin: "30px", fontSize: "large", borderRadius: "20px" }}
        onClick={() => {
          if (!userInfoState.isLoggedIn) window.alert("로그인을 해주세요!");
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
              padding: "15px 0px",
              width: "40%",
              margin: "0 auto",
            }}
          >
            내가 참가한 미션
            <span style={{ color: `${theme.subGreen}`, paddingLeft: "10px" }}>
              {myMissionData?.length || 0}
            </span>
          </h2>
          {myMissionData?.length && (
            <MyMissionSection>
              {myMissionData?.map((mission, index) => (
                <MyCard
                  key={index}
                  id={mission.id}
                  username={mission.username}
                  title={mission.title}
                  duration={mission.duration}
                  frequency={mission.frequency}
                  people={mission.participants}
                  photoUrl={mission.photoUrl}
                />
              ))}
            </MyMissionSection>
          )}
        </div>
      )}
      {totalMissionData.length === 0 && !isLoading && (
        <NoLoginContent style={{ padding: "10%" }}>
          <span>❌</span>
          <h1>{noDataMessage}</h1>
          <p>미션을 생성해보세요!</p>
        </NoLoginContent>
      )}
      <MainSection>
        {totalMissionData?.map((mission, index) => (
          <Card
            key={index}
            id={mission.id}
            title={mission.title}
            username={mission.username}
            minPar={mission.minParticipants}
            par={mission.participants}
            duration={mission.duration}
            status={mission.status}
            frequency={mission.frequency}
            photoUrl={mission.photoUrl}
          />
        ))}
        <p>여기가 구분선</p>
        <button onClick={() => fetchNextPage()}>Load More</button>
        {isFetchingNextPage
          ? "로딩 중"
          : hasNextPage
          ? "미션을 더 보고싶으면, 스크롤을 내려주세요!"
          : "더 이상 미션이 존재하지 않습니다!"}
      </MainSection>
    </Layout>
  );
};

export default MainPage;

const MainSection = styled.section`
  min-height: 100vh;
  padding: 3vh;
  width: 70%;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(15vw, 1fr));
  @media screen and (max-width: 1600px) {
    grid-template-columns: repeat(auto-fill, minmax(20vw, 1fr));
  }
  @media screen and (max-width: 1080px) {
    grid-template-columns: repeat(auto-fill, minmax(30vw, 1fr));
  }
  gap: 20px; /* Adjust the gap between cards */
`;

const MyMissionSection = styled.section`
  padding-bottom: 3vh;
  margin: 0 auto;
  margin-bottom: 5vh;
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
