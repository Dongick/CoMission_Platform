import styled from "styled-components";
import { theme } from "../styles/theme";
import { StyledCard } from "./Card";
import { title } from "process";

interface CardProps {
  title: string;
  author: string;
  people: number;
  id: number;
}

const MyCard = (props: CardProps) => {
  return (
    <MyStyledCard>
      {props.author}
      {props.title}
      {props.people}
    </MyStyledCard>
  );
};

export default MyCard;

const MyStyledCard = styled(StyledCard)`
  background-color: white;
  height: 90%;
  width: 12vw;
  margin: 0;
  margin-right: 2vw;
  padding: 10px;
  flex: 0 0 auto;
`;
