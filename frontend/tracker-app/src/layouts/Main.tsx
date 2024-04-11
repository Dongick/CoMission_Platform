import { ReactNode } from "react";
import styled from "styled-components";
const StyledMain = styled.main`
  flex: 1;
`;
interface Props {
  children: ReactNode;
}

const Main = ({ children }: Props) => {
  return <StyledMain>{children}</StyledMain>;
};
export default Main;
