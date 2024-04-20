import MoveToTop from "../components/MoveToTop";
import Footer from "./Footer";
import Header from "./Header";
import Main from "./Main";
import { ReactNode } from "react";
import styled from "styled-components";

interface Props {
  children: ReactNode;
  footer?: boolean;
}
const StyledLayout = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
`;

const Layout = ({ children, footer = true }: Props) => {
  return (
    <StyledLayout>
      <MoveToTop />
      <Header />
      <Main>{children}</Main>
      {footer && <Footer />}
    </StyledLayout>
  );
};

export default Layout;
