import styled from "styled-components";
import { theme } from "../styles/theme";
import UserMenu from "../components/UserMenu";
const StyledHeader = styled.header`
  background-color: white;
  color: ${theme.mainBlue};
  font-family: "ubuntuBold";
  font-size: 4rem;
  padding: 4rem;
  margin: 0 auto;
  font-style: italic;
  @media screen and (max-width: 1400px) {
    font-size: 3rem;
    padding: 3rem;
  }
  @media screen and (max-width: 1200px) {
    font-size: 2rem;
    padding: 2rem;
  }
`;

const Header = () => {
  return (
    <div style={{ position: "relative" }}>
      <StyledHeader>
        <a href="/">Comission Platform</a>
        <UserMenu />
      </StyledHeader>
    </div>
  );
};
export default Header;
