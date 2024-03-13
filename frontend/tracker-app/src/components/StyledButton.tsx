import styled from "styled-components";
import { theme } from "../styles/theme";
interface ButtonProps {
  bgcolor?: string;
  color?: string;
}

const StyledButton = styled.button<ButtonProps>`
  background-color: ${(props) => props.bgcolor || "black"};
  color: ${(props) => props.color || "white"};
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  &:hover {
    box-shadow: ${theme.boxShadow};
    filter: brightness(1.1);
  }
`;

export default StyledButton;
