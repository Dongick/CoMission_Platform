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
  min-width: 30%;
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
  height: 5%;
`;
export const NavButton = styled.button<{ clicked: string }>`
  height: 100%;
  background-color: white;
  font-family: ${(props) => (props.clicked === "true" ? "notoBold" : "noto")};
  border-bottom: ${(props) =>
    props.clicked === "true" ? "2px solid #1a1b1e" : "none"};
  font-size: large;
  padding: 10px;
  margin-right: 10px;
`;
export const MainSection = styled.section`
  min-height: 80vh;
  padding: 3vh 3vw;
  width: 650px;
  margin: 0 auto;
  background-color: ${theme.mainGray};
`;

export const MissionContent = styled.div`
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
`;

export const MissionSubTitle = styled.h2`
  padding: 15px 0px;
  text-align: left;
  font-family: "gmarket1";
  font-size: 1.3rem;
`;

export const MissionSubContent = styled.div`
  text-align: left;
  padding: 1rem;
  min-height: 30vh;
`;

export const HrDivider = styled.hr`
  margin: 1.5rem 0;
  border-top: 1.5px solid #393939;
`;
