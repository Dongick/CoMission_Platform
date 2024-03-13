import { theme } from "../styles/theme";
import StyledButton from "../components/StyledButton";
import Layout from "../layouts/Layout";
import styled from "styled-components";
import sectionSVG from "../assets/img/wave-haikei.svg";
const SearchSection = styled.section`
  background-image: url(${sectionSVG});
  background-size: cover; /* 원하는 크기로 이미지를 맞춥니다. */
  background-position: center; /* 이미지를 가운데 정렬합니다. */
  height: 25vh;
  padding: 10px;
  font-size: 2rem;
  font-weight: 400;
  color: #333;
  margin-bottom: 5vh;
`;
const MainPage = () => {
  const printMsg = () => {
    console.log("clicked!!");
  };
  return (
    <Layout>
      <SearchSection>
        <h1>미션을 검색해보세요!</h1>
        <div
          style={{
            display: "flex",
            flexDirection: "row",
            justifyContent: "center",
            padding: "20px",
          }}
        >
          <input
            type="text"
            placeholder="미션명 검색하기"
            style={{ width: "25%", borderRadius: "5px", outline: " none" }}
          />
          <StyledButton bgcolor={theme.subGreen} color="white">
            검색
          </StyledButton>
        </div>
      </SearchSection>

      <a href="http://localhost:8080/login/oauth2/code/google">Google Login</a>
      <br />
      <a href="http://localhost:8080/login/oauth2/code/naver">Naver Login</a>
    </Layout>
  );
};

export default MainPage;
