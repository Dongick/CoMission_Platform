import styled from "styled-components";
import { theme } from "../styles/theme";
import { useNavigate } from "react-router-dom";
import noImg from "../assets/img/no-pictures.png";
interface CardProps {
  id: string;
  title: string;
  username: string;
  minPar: number;
  par: number;
  duration: number;
  status: string;
  photoUrl: string;
  frequency: string;
}

const Card = ({
  id,
  title,
  username,
  minPar,
  par,
  duration,
  status,
  photoUrl,
  frequency,
}: CardProps) => {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate(`/mission/${id}/detail`);
  };
  return (
    <div>
      <StyledCard onClick={handleClick}>
        {photoUrl ? (
          <img src={photoUrl} alt="Img" width="100%" height="50%" />
        ) : (
          <img src={noImg} alt="Img" width="100%" height="60%" />
        )}
        <ContentDiv>
          <div>
            <h2
              style={{
                fontFamily: "notoBold",
                fontSize: "1.2rem",
                padding: "10px",
                whiteSpace: "nowrap",
                overflow: "hidden",
                textAlign: "left",
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
              justifyContent: "space-evenly",
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
              {username}
            </p>
            <p
              style={{
                overflow: "hidden",
                whiteSpace: "nowrap",
                textOverflow: "ellipsis",
              }}
            >
              👨‍👧‍👧인원: {par}/{minPar} 명
            </p>
          </div>
          <div
            style={{
              padding: "5px",
              display: "flex",
              flexDirection: "row",
              justifyContent: "space-evenly",
              maxWidth: "100%",
            }}
          >
            <div
              style={{
                padding: "5px",
                fontSize: "0.9rem",
                overflow: "hidden",
                whiteSpace: "nowrap",
                textOverflow: "ellipsis",
              }}
            >
              📆 진행 기간: {duration}일
            </div>
            <div
              style={{
                padding: "5px",
                fontSize: "0.9rem",
                overflow: "hidden",
                whiteSpace: "nowrap",
                textOverflow: "ellipsis",
              }}
            >
              🖌 인증 주기: {frequency}
            </div>
          </div>
        </ContentDiv>
      </StyledCard>
    </div>
  );
};
export default Card;

export const StyledCard = styled.section`
  border-radius: 10px;
  margin-bottom: 5vh;
  box-shadow: ${theme.boxShadow};
  display: flex;
  flex-direction: column;
  cursor: pointer;
`;
export const ContentDiv = styled.div`
  padding: 5px;
  font-family: "noto";
  text-align: left;
  overflow: hidden;
  p,
  div {
    text-align: left;
  }
`;
