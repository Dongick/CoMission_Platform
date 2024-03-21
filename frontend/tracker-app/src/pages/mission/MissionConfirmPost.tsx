import styled from "styled-components";
import Layout from "../../layouts/Layout";
import { BannerSection, Navbar, NavButton, MainSection } from "./MissionStyles";
import { Link, useParams, useLocation } from "react-router-dom";
import ConfirmPostList from "../../components/ConfirmPostList";
import { userInfo } from "../../recoil";
import { useRecoilState } from "recoil";
import { MissionConfirmPostType } from "../../types";
const examplePosts: MissionConfirmPostType[] = [
  {
    date: new Date("2024-03-15T12:30:00"),
    completed: true,
    photo: "http://example.com/photo1.jpg",
    text: "Mission completed successfully.",
  },
  {
    date: new Date("2024-03-16"),
    completed: false,
    photo: "http://example.com/photo2.jpg",
    text: "Encountered some difficulties during the mission.",
  },
];
const MissionConfirmPost = () => {
  const { cardId } = useParams();
  const location = useLocation();
  const detailURL = `/mission/${cardId}/detail`;
  const confirmURL = `/mission/${cardId}/confirm-post`;
  const [userInfoState, setUserInfoState] = useRecoilState(userInfo);

  return (
    <Layout>
      <BannerSection>zz</BannerSection>
      <Navbar>
        <Link to={detailURL}>
          <NavButton clicked="false">🔎 미션 소개</NavButton>
        </Link>
        <Link to={confirmURL}>
          <NavButton clicked="true">📜 미션 인증글</NavButton>
        </Link>
      </Navbar>
      <MainSection>
        {!userInfoState.isLoggedIn ? (
          <ConfirmPostList postList={examplePosts} />
        ) : (
          <NoLoginContent>
            <span>❌</span>
            <h1>미션에 가입해야 조회가 가능합니다.</h1>
            <p>미션에 참가해보세요!</p>
          </NoLoginContent>
        )}
      </MainSection>
    </Layout>
  );
};

export default MissionConfirmPost;

const NoLoginContent = styled.div`
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
