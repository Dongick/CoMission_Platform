import styled from "styled-components";
import StyledButton from "./StyledButton";
import LoginModal from "./LoginModal";
import { useState } from "react";
import { useRecoilState } from "recoil";
import { userInfo } from "../recoil";
import { postData } from "../axios";
import { useNavigate } from "react-router";
import useLogout from "../useLogout";
const UserMenu = () => {
  const navigate = useNavigate();
  const logout = useLogout();
  const [showLoginModal, setShowLoginModal] = useState<boolean>(false);
  const [userInfoState, setUserInfoState] = useRecoilState(userInfo);
  const handleLoginClick = () => {
    setShowLoginModal(true);
  };
  const handleCloseClick = () => {
    setShowLoginModal(false);
  };
  const logoutHandler = () => {
    postData<string, string>("/api/user/logout", "")
      .then((data) => {
        console.log(data);
        logout();
      })
      .catch((error) => {
        if (typeof error === "string") {
          alert("토큰 재발급 실패! 재로그인 해주세요");
          logout();
        }
      });
  };
  const myInfoButton = (
    <StyledButton
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
      {userInfoState.isLoggedIn}
      {userInfoState.isLoggedIn ? (
        <div>
          {myInfoButton}
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
