import Footer from "./Footer";
import Header from "./Header";
import Main from "./Main";
import { ReactNode } from "react";
import styled from "styled-components";

interface Props {
  children: ReactNode;
}
const StyledLayout = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
`;

const Layout = ({ children }: Props) => {
  return (
    <StyledLayout>
      <Header />
      <Main>{children}</Main>
      <Footer />
    </StyledLayout>
  );
};

export default Layout;
