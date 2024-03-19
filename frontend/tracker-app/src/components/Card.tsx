import styled from "styled-components";
import { theme } from "../styles/theme";
import Img from "../assets/img/roadmap-77.png";
import { useNavigate } from "react-router-dom";
import { MissionType } from "../types";
interface CardProps {
  title: string;
  author: string;
  people: number;
  id: number;
  missionData: MissionType;
}
const StyledCard = styled.section`
  height: 40vh;
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
  height: 60%;
  width: 100%;
`;
const ContentDiv = styled.div`
  padding: 15px;
  font-family: "noto";
  height: 40%;
  overflow: hidden;
`;
const Card = ({ title, author, people, id, missionData }: CardProps) => {
  const navigate = useNavigate();

  // todo: react-query에 저장되어 있는 값으로 받기
  const handleClick = () => {
    navigate(`/mission/${id}/detail`, { state: { mission: missionData } });
  };
  return (
    <div onClick={handleClick}>
      <StyledCard>
        <ImgDiv />
        <ContentDiv>
          <div>
            <h2
              style={{
                fontFamily: "notoBold",
                fontSize: "1.2rem",
                paddingBottom: "5px",
                whiteSpace: "nowrap",
                overflow: "hidden",
                textOverflow: "ellipsis",
              }}
            >
              {title}
            </h2>
          </div>
          <div
            style={{
              padding: "5px",
              display: "flex",
              flexDirection: "row",
              justifyContent: "space-around",
              maxWidth: "100%",
            }}
          >
            <p
              style={{
                overflow: "hidden",
                whiteSpace: "nowrap",
                textOverflow: "ellipsis",
              }}
            >
              {author}
            </p>
            <p
              style={{
                overflow: "hidden",
                whiteSpace: "nowrap",
                textOverflow: "ellipsis",
              }}
            >
              필요 인원: {people}명
            </p>
          </div>
          <div
            style={{
              fontFamily: "gmarket1",
              padding: "5px",
              // height: "50%",
              overflow: "hidden",
              whiteSpace: "wrap",
              textOverflow: "ellipsis",
            }}
          >
            ??명이 더 모이면
            <br /> 미션이 시작됩니다
          </div>
        </ContentDiv>
      </StyledCard>
    </div>
  );
};
export default Card;
