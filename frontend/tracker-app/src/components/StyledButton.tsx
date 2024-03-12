import styled from "styled-components";

interface ButtonProps {
  bgColor?: string;
  color?: string;
  onClick?: () => void;
}

const StyledButton = styled.button<ButtonProps>`
  background-color: ${(props) => props.bgColor || "black"};
  color: ${(props) => props.color || "white"};
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
`;

export default StyledButton;
