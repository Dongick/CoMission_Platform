import styled from "styled-components";
import { theme } from "../styles/theme";
interface ButtonProps {
  bgcolor?: string;
  color?: string;
  onClick?: () => void;
}

const StyledButton = styled.button<ButtonProps>`
  background-color: ${(props) => props.bgcolor || "black"};
  color: ${(props) => props.color || "white"};
  padding: 10px 20px;
  border: none;
  font-family: "gmarket2";
  border-radius: 8px;
  cursor: pointer;
  &:hover {
    box-shadow: ${theme.boxShadow};
  }
  @media screen and (max-width: 1024px) {
    padding: 8px 16px;
    font-size: 14px;
  }
  @media screen and (max-width: 768px) {
    padding: 5px 10px;
    font-size: 10px;
  }
`;

export default StyledButton;
