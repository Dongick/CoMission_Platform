import { ReactNode } from "react";
import styled from "styled-components";
import { theme } from "../styles/theme";
const StyledMain = styled.main`
  /* background-color: #f6f9f0; */
  flex: 1;
`;
interface Props {
  children: ReactNode;
}

const Main = ({ children }: Props) => {
  return <StyledMain>{children}</StyledMain>;
};
export default Main;
