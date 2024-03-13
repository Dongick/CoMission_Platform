import styled from "styled-components";
import { theme } from "../styles/theme";
import Img from "../assets/img/roadmap-77.png";

interface CardProps {
  title: string;
  author: string;
  people: number;
}
const StyledCard = styled.section`
  height: 35vh;
  width: 15vw;
  border-radius: 10px;
  margin-bottom: 5vh;
  box-shadow: ${theme.boxShadow};
  display: flex;
  flex-direction: column;
`;
const ImgDiv = styled.div`
  background-image: url(${Img});
  background-size: cover;
  background-position: center;
  height: 65%;
  width: 100%;
`;
const ContentDiv = styled.div`
  padding: 15px;
  font-family: "noto";
  height: 35%;
`;
const Card = ({ title, author, people }: CardProps) => {
  return (
    <StyledCard>
      <ImgDiv />
      <ContentDiv>
        <div>
          <h2 style={{ fontFamily: "notoBold", fontSize: "1.2rem" }}>
            {title}
          </h2>
        </div>
        <div
          style={{
            padding: "15px",
            display: "flex",
            flexDirection: "row",
            justifyContent: "space-around",
          }}
        >
          <p>리더: {author}</p>
          <p>최소 인원: {people}명</p>
        </div>
      </ContentDiv>
    </StyledCard>
  );
};
export default Card;
