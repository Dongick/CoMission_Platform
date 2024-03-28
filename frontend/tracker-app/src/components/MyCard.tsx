import styled from "styled-components";
import { theme } from "../styles/theme";
import { StyledCard, ImgDiv, ContentDiv } from "./Card";
import exampleImg from "../assets/img/wave-haikei.svg";
import { useNavigate } from "react-router-dom";
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
      <MyImgDiv />
      <MyContentDiv>
        <p>{props.title}</p>
        <p>{props.duration}</p>
        <p>{props.frequency}</p>
        <p>멤버 {props.people}</p>
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
  padding: 5px;
  overflow: hidden;
  p {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    /* text-align: left; */
    font-family: "noto";
    font-size: 0.8rem;
  }
  & > p:first-child {
    font-family: "notoBold";
    font-size: 1rem;
  }
`;
