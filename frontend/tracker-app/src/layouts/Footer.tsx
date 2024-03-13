import styled from "styled-components";
import { theme } from "../styles/theme";
const StyledFooter = styled.footer`
  background-color: ${theme.mainBlue};
  color: white;
  flex: 0 0 15%;
  padding: 20px;
`;
const Footer = () => {
  return (
    <>
      <StyledFooter>This is Footer</StyledFooter>
    </>
  );
};
export default Footer;
