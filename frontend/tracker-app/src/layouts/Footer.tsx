import styled from "styled-components";
import { theme } from "../styles/theme";
const StyledFooter = styled.footer`
  position: relative;
  background-color: ${theme.mainBlue};
  color: white;
  flex: 0 0 15%;
  padding: 20px;
  margin-top: 20vh;
  font-family: "ubuntuBold";
  font-size: 1.2rem;
`;
const Footer = () => {
  return (
    <>
      <StyledFooter>
        <div
          style={{
            position: "absolute",
            bottom: "10px",
            left: "50%",
            transform: "translateX(-50%)",
          }}
        >
          Comission Platform Project
        </div>
      </StyledFooter>
    </>
  );
};
export default Footer;
