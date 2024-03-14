import React from "react";
import styled from "styled-components";
import StyledButton from "./StyledButton";
interface UserMenuProps {
  isLogin: boolean;
}
const UserMenu = ({ isLogin }: UserMenuProps) => {
  // Your component logic here

  return (
    <Wrapper>
      <StyledButton
        bgcolor="##363636"
        color="black"
        style={{ border: "0.5px solid #363636" }}
      >
        로그인
      </StyledButton>
    </Wrapper>
  );
};

export default UserMenu;

const Wrapper = styled.div`
  position: absolute;
  top: 40%;
  right: 10%;
`;
