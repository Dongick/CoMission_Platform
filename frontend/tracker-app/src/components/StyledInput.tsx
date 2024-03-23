import styled from "styled-components";
import { theme } from "../styles/theme";

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  size?: number;
}

const StyledInput = styled.input<InputProps>`
  width: ${(props) => props.size}vw;
  border-radius: 10px;
  outline: none;
  &:focus-visible {
    outline: 1px solid ${theme.subGreen};
    box-shadow: ${theme.boxShadowHover};
  }
`;

const Input = ({ size, ...props }: InputProps) => {
  return <StyledInput size={size} {...props} />;
};

export default Input;
