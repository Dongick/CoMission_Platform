import styled from "styled-components";
import { StyledCard, ContentDiv } from "./Card";
import { useNavigate } from "react-router-dom";
import noImg from "../assets/img/no-pictures.png";

interface CardProps {
  title: string;
  username: string;
  duration: number;
  frequency: string;
  people: number;
  id: string;
  photoUrl: string;
}

const MyCard = (props: CardProps) => {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate(`/mission/${props.id}/detail`);
  };
  return (
    <MyStyledCard onClick={handleClick}>
      {props.photoUrl ? (
        <img
          src={props.photoUrl}
          alt="Img"
          width="100%"
          height="55%"
          style={{ margin: "0 auto" }}
        />
      ) : (
        <img
          src={noImg}
          alt="Img"
          width="100%"
          height="55%"
          style={{ margin: "0 auto" }}
        />
      )}
      <MyContentDiv>
        <p>{props.title}</p>
        <p>🕧 {props.duration}일</p>
        <p> 🖌 {props.frequency}</p>
        <p>👨‍👧‍👧 {props.people}명</p>
      </MyContentDiv>
    </MyStyledCard>
  );
};

export default MyCard;

const MyStyledCard = styled(StyledCard)`
  background-color: white;
  height: 100%;
  width: 10vw;
  margin: 0;
  margin-right: 2vw;
  flex: 0 0 auto;
`;

const MyContentDiv = styled(ContentDiv)`
  overflow: hidden;
  height: auto;
  p {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-family: "noto";
    font-size: 0.8rem;
    @media (max-width: 1080px) {
      font-size: 0.7rem;
    }
  }
  & > p:first-child {
    font-family: "notoBold";
    font-size: 1rem;
    @media (max-width: 1080px) {
      font-size: 0.9rem;
    }
  }
`;
