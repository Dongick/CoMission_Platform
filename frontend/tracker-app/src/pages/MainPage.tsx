import { theme } from "../styles/theme";
import StyledButton from "../components/StyledButton";
import Layout from "../layouts/Layout";
import styled from "styled-components";
import sectionSVG from "../assets/img/wave-haikei.svg";
import Card from "../components/Card";
import { userInfo } from "../recoil";
import { useRecoilState } from "recoil";
import { useNavigate } from "react-router";
import Input from "../components/StyledInput";
const MainPage = () => {
  const navigate = useNavigate();
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
      title: "Title 2",
      author: "Author 2",
      participants: 5,
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
      title: "Title 3",
      author: "Author 3",
      participants: 2,
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
      title: "Title 3",
      author: "Author 3",
      participants: 2,
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
    // Add more card data as needed
  ];
  const [userInfoState, setUserInfoState] = useRecoilState(userInfo);
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
        style={{ margin: "10px", fontSize: "large", borderRadius: "20px" }}
        onClick={() => {
          if (userInfoState.isLoggedIn) window.alert("로그인을 해주세요!");
          else {
            navigate("/mission-create");
          }
        }}
      >
        새로운 미션 등록
      </StyledButton>
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
  margin-bottom: 3vh;
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
