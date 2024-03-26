import React from "react";
import styled from "styled-components";
import StyledButton from "./StyledButton";
import LoginModal from "./LoginModal";
import { useState } from "react";
interface UserMenuProps {
  isLogin: boolean;
}
const UserMenu = ({ isLogin = false }: UserMenuProps) => {
  const [showLoginModal, setShowLoginModal] = useState<boolean>(false);

  const handleLoginClick = () => {
    setShowLoginModal(true);
  };
  const handleCloseClick = () => {
    setShowLoginModal(false);
  };
  const logoutHandler = () => {
    // 로그아웃 프로세스
    window.alert("logout!");
  };
  const myInfo = (
    <StyledButton
      onClick={logoutHandler}
      bgcolor="##363636"
      color="black"
      style={{
        border: "0.5px solid #363636",
        padding: "10px 15px",
        marginRight: "10px",
      }}
    >
      내 정보
    </StyledButton>
  );
  return (
    <Wrapper>
      {isLogin ? (
        <div>
          {myInfo}
          <StyledButton
            onClick={logoutHandler}
            bgcolor="##363636"
            color="black"
            style={{ border: "0.5px solid #363636", padding: "10px 15px" }}
          >
            로그아웃
          </StyledButton>
        </div>
      ) : (
        <StyledButton
          onClick={handleLoginClick}
          bgcolor="##363636"
          color="black"
          style={{ border: "0.5px solid #363636", padding: "10px 15px" }}
        >
          로그인
        </StyledButton>
      )}
      {showLoginModal && <LoginModal onClose={handleCloseClick} />}
    </Wrapper>
  );
};

export default UserMenu;

const Wrapper = styled.div`
  position: absolute;
  top: 40%;
  right: 15%;
`;
