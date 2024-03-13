import { theme } from "../styles/theme";
import StyledButton from "../components/StyledButton";
import Layout from "../layouts/Layout";
import styled from "styled-components";
import sectionSVG from "../assets/img/wave-haikei.svg";
import Card from "../components/Card";

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
const Input = styled.input`
  width: 25vw;
  border-radius: 10px;
  outline: none;
  &:focus-visible {
    outline: 1px solid ${theme.subGreen};
    box-shadow: ${theme.boxShadowHover};
  }
`;
const MainSection = styled.section`
  min-height: 100vh;
  padding: 5vh;
  width: 70%;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px; /* Adjust the gap between cards */
`;
const MainPage = () => {
  const cardsData = [
    { title: "Title 1", author: "Author 1", people: 3 },
    { title: "Title 2", author: "Author 2", people: 5 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    { title: "Title 3", author: "Author 3", people: 2 },
    // Add more card data as needed
  ];
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
          <Input type="text" placeholder="미션명 검색하기" />
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
        {cardsData.map((card, index) => (
          <Card
            key={index}
            title={card.title}
            author={card.author}
            people={card.people}
          />
        ))}
      </MainSection>
      {/* <a href="http://localhost:8080/login/oauth2/code/google">Google Login</a>
      <br />
      <a href="http://localhost:8080/login/oauth2/code/naver">Naver Login</a> */}
    </Layout>
  );
};

export default MainPage;
