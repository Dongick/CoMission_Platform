import styled from "styled-components";
import { SearchSection } from "../MainPage";
import { theme } from "../../styles/theme";

export const BannerSection = styled(SearchSection)`
  background-color: #25262b;
  background-image: none;
  color: white;
  height: 30vh;
  flex-direction: row;
  align-items: center;
  margin-bottom: 0;
`;
export const TitleDiv = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-start;
  height: 70%;
  font-size: 1.7rem;

  & > div:nth-child(n + 2) {
    font-size: 1rem;
    color: #dee2e6;
    display: flex;
    justify-content: flex-start;
    font-family: "noto";
  }
`;
export const Navbar = styled.nav`
  border-bottom: 2px solid #e9ecef;
  height: 5vh;
`;
export const NavButton = styled.button<{ clicked: boolean }>`
  height: 100%;
  background-color: white;
  font-family: ${(props) => (props.clicked ? "notoBold" : "noto")};
  border-bottom: ${(props) =>
    props.clicked === true ? "2px solid #1a1b1e" : "none"};
  font-size: large;
  padding: 10px;
  margin-right: 10px;
`;
export const MainSection = styled.section`
  min-height: 100vh;
  padding: 3vh;
  width: 50%;
  margin: 0 auto;
  background-color: ${theme.mainGray};
`;
