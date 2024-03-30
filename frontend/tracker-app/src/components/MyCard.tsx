import styled from "styled-components";
import { theme } from "../styles/theme";
import { StyledCard, ImgDiv, ContentDiv } from "./Card";
import exampleImg from "../assets/img/wave-haikei.svg";
import { useNavigate } from "react-router-dom";
import noImg from "../assets/img/no-pictures.png";

interface CardProps {
  title: string;
  username: string;
  duration: number;
  frequency: string;
  people: number;
  id: string;
}

const MyCard = (props: CardProps) => {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate(`/mission/${props.id}/detail`);
  };
  return (
    <MyStyledCard onClick={handleClick}>
      <img src={noImg} alt="" width="100%" height="60%" />
      {/* <MyImgDiv /> */}
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
  width: 12vw;
  margin: 0;
  margin-right: 2vw;
  flex: 0 0 auto;
`;

const MyImgDiv = styled(ImgDiv)`
  background-image: url(${exampleImg});
  /* height: 60%; */
`;

const MyContentDiv = styled(ContentDiv)`
  padding: 10px;
  overflow: hidden;
  p {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-family: "noto";
    font-size: 0.9rem;
  }
  & > p:first-child {
    font-family: "notoBold";
    font-size: 1rem;
  }
`;
