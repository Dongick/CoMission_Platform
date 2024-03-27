import React from "react";
import styled from "styled-components";
import StyledButton from "./StyledButton";
import LoginModal from "./LoginModal";
import { useState } from "react";
import { useSetRecoilState } from "recoil";
import { userInfo } from "../recoil";
import { postData } from "../axios";
import { useNavigate } from "react-router";
interface UserMenuProps {
  isLogin: boolean;
}
const UserMenu = ({ isLogin = false }: UserMenuProps) => {
  const navigate = useNavigate();
  const [showLoginModal, setShowLoginModal] = useState<boolean>(false);
  const setUserInfoState = useSetRecoilState(userInfo);
  const handleLoginClick = () => {
    setShowLoginModal(true);
  };
  const handleCloseClick = () => {
    setShowLoginModal(false);
  };
  const logoutHandler = () => {
    const accessToken = localStorage.getItem("accessToken");
    if (accessToken) {
      postData<string, string>("/api/user/logout", accessToken)
        .then((data) => {
          localStorage.removeItem("accessToken");
          setUserInfoState({
            isLoggedIn: false,
            user_id: "",
            user_email: "",
          });
          alert(`${data}, 세션이 만료되었습니다. 로그인 해주세요`);
          navigate("/");
        })
        .catch((error) => {
          console.error(`로그아웃 에러 발생: ${error}`);
          alert(`로그아웃 에러, ${error}`);
        });
    }
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
