import styled from "styled-components";
import { theme } from "../styles/theme";
const StyledHeader = styled.header`
  background-color: white;
  color: ${theme.mainBlue};
  flex: 0 0 15%;
  font-family: "ubuntuBold";
  font-size: 6vh;
  padding: 5vh;
  margin: 0 auto;
`;

const Header = () => {
  return (
    <>
      <StyledHeader>Comission Platform</StyledHeader>
    </>
  );
};
export default Header;
