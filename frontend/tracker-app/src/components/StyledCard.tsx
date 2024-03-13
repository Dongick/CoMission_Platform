import styled from "styled-components";
import { theme } from "../styles/theme";
import Img from "../assets/img/roadmap-77.png";
interface CardProps {
  bgcolor?: string;
  color?: string;
}

const StyledCard = styled.section<CardProps>`
  background-image: url(${Img});
  background-size: cover;
  background-position: center;
  height: 15vh;
  width: 15vw;
`;

export default StyledCard;
