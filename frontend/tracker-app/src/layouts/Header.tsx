import styled from "styled-components";
import { theme } from "../styles/theme";
import UserMenu from "../components/UserMenu";
import { Link } from "react-router-dom";
const StyledHeader = styled.header`
  background-color: white;
  color: ${theme.mainBlue};
  flex: 0 0 15%;
  font-family: "ubuntuBold";
  font-size: 6vh;
  padding: 5vh;
  margin: 0 auto;
  font-style: italic;
`;

const Header = () => {
  return (
    <div style={{ position: "relative" }}>
      <StyledHeader>
        <a href="/">Comission Platform</a>
      </StyledHeader>
      <UserMenu />
    </div>
  );
};
export default Header;
