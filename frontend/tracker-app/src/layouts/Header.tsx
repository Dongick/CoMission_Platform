import styled from "styled-components";
import { theme } from "../styles/theme";
import UserMenu from "../components/UserMenu";
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
        <h1>Comission Platform</h1>
      </StyledHeader>
      <UserMenu isLogin={true} />
    </div>
  );
};
export default Header;
