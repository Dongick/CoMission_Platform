import React from "react";
import styled from "styled-components";
import StyledButton from "./StyledButton";
import LoginModal from "./LoginModal";
import { useState } from "react";
interface UserMenuProps {
  isLogin: boolean;
}
const UserMenu = ({ isLogin }: UserMenuProps) => {
  const [showLoginModal, setShowLoginModal] = useState<boolean>(false);

  const handleLoginClick = () => {
    setShowLoginModal(true);
  };
  const handleCloseClick = () => {
    setShowLoginModal(false);
  };
  return (
    <Wrapper>
      <StyledButton
        onClick={handleLoginClick}
        bgcolor="##363636"
        color="black"
        style={{ border: "0.5px solid #363636", padding: "10px 15px" }}
      >
        로그인
      </StyledButton>
      {showLoginModal && <LoginModal onClose={handleCloseClick} />}
    </Wrapper>
  );
};

export default UserMenu;

const Wrapper = styled.div`
  position: absolute;
  top: 40%;
  right: 10%;
`;
