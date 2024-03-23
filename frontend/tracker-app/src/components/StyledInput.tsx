import styled from "styled-components";
import { theme } from "../styles/theme";

interface InputProps
  extends React.InputHTMLAttributes<HTMLInputElement | HTMLTextAreaElement> {
  size?: number;
}

const StyledInput = styled.input<InputProps>`
  width: ${(props) => props.size}vw;
  border-radius: 10px;
  outline: none;
  padding: 5px;
  &:focus-visible {
    outline: 1px solid ${theme.subGreen};
    box-shadow: ${theme.boxShadowHover};
  }
`;

const StyledTextArea = styled.textarea<InputProps>`
  width: 100%;
  padding: 5px;
  border-radius: 3px;
  max-width: 100%;
  text-align: left;
  min-height: 100px;
  max-height: 500px;
  &:focus-visible {
    outline: 1px solid black;
    box-shadow: ${theme.boxShadowHover};
  }
`;

// input tag or textarea tag 사용가능
const Input = ({ size = 10, ...props }: InputProps) => {
  if (props.type === "textarea") {
    return <StyledTextArea size={size} {...props} />;
  } else {
    return <StyledInput size={size} {...props} />;
  }
};

export default Input;
