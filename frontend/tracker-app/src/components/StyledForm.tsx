import styled from "styled-components";

interface FormProps extends React.FormHTMLAttributes<HTMLFormElement> {
  children: React.ReactNode;
}
const StyledForm = styled.form`
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
`;

const Form = ({ children, ...props }: FormProps) => {
  return <StyledForm {...props}>{children}</StyledForm>;
};

export default Form;
