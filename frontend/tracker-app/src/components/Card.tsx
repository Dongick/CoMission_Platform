import styled from "styled-components";
import { theme } from "../styles/theme";
import Img from "../assets/img/roadmap-77.png";
import { useNavigate } from "react-router-dom";
interface CardProps {
  id: number;
  title: string;
  author: string;
  minPar: number;
  par: number;
  duration: number;
}

const Card = ({ id, title, author, minPar, par, duration }: CardProps) => {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate(`/mission/${id}/detail`, { state: { title } });
  };

  return (
    <div>
      <StyledCard onClick={handleClick}>
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
              필요 인원: {minPar}명
            </p>
          </div>
          <div
            style={{
              fontFamily: "gmarket1",
              padding: "5px",
              overflow: "hidden",
              whiteSpace: "nowrap",
              textOverflow: "ellipsis",
            }}
          >
            미션 진행 기간: {duration}일
          </div>
        </ContentDiv>
      </StyledCard>
    </div>
  );
};
export default Card;

export const StyledCard = styled.section`
  height: 40vh;
  width: 15vw;
  border-radius: 10px;
  margin-bottom: 5vh;
  box-shadow: ${theme.boxShadow};
  display: flex;
  flex-direction: column;
  cursor: pointer;
`;
export const ImgDiv = styled.div`
  background-image: url(${Img});
  background-size: cover;
  background-position: center;
  min-height: 60%;
  width: 100%;
`;
export const ContentDiv = styled.div`
  padding: 15px;
  font-family: "noto";
  overflow: hidden;
`;
