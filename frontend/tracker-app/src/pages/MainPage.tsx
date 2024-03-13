import { theme } from "../styles/theme";
import StyledButton from "../components/StyledButton";
import Layout from "../layouts/Layout";
import styled from "styled-components";
import sectionSVG from "../assets/img/wave-haikei.svg";
import StyledCard from "../components/StyledCard";

const SearchSection = styled.section`
  background-image: url(${sectionSVG});
  background-size: cover; /* 원하는 크기로 이미지를 맞춥니다. */
  background-position: center; /* 이미지를 가운데 정렬합니다. */
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
  background-color: ${theme.mainGray};
  min-height: 50vh;
  padding: 5vh;
  width: 70%;
  margin: 0 auto;
`;
const MainPage = () => {
  const printMsg = () => {
    console.log("clicked!!");
  };
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
          <input
            type="text"
            placeholder="미션명 검색하기"
            style={{ width: "25vw", borderRadius: "10px", outline: " none" }}
          />
          <StyledButton
            bgcolor={theme.subGreen}
            color="white"
            style={{ fontSize: "medium" }}
          >
            검색
          </StyledButton>
        </div>
      </SearchSection>
      <MainSection>
        <StyledCard></StyledCard>
        <StyledCard></StyledCard>
        <StyledCard></StyledCard>
        <StyledCard></StyledCard>
        <StyledCard></StyledCard>
        <StyledCard></StyledCard>
        <StyledCard></StyledCard>
        <StyledCard></StyledCard>
        <StyledCard></StyledCard>
      </MainSection>
      {/* <a href="http://localhost:8080/login/oauth2/code/google">Google Login</a>
      <br />
      <a href="http://localhost:8080/login/oauth2/code/naver">Naver Login</a> */}
    </Layout>
  );
};

export default MainPage;
