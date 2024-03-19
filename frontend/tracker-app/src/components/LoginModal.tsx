import styled from "styled-components";
import { theme } from "../styles/theme";
import googleLogin from "../assets/img/google-login.svg";
import naverLogin from "../assets/img/naver-login.png";
interface LoginDivProps {
  naver?: string;
}
interface LoginModalProps {
  onClose: () => void;
}
const ModalTitle = styled.h1`
  font-size: 1.5rem;
  font-family: "notoBold";
  color: ${theme.mainBlue};
  padding: 10px;
`;
const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
`;

const ModalContent = styled.div`
  position: relative;
  background-color: white;
  padding: 20px;
  min-height: 30%;
  border-radius: 8px;
`;
const LoginDiv = styled.div<LoginDivProps>`
  background-image: url(${(props) => (props.naver ? naverLogin : googleLogin)});
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  height: 40px;
  width: 300px;
`;
const LoginModal = ({ onClose }: LoginModalProps) => {
  const handleOverlayClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };
  return (
    <ModalOverlay onClick={handleOverlayClick}>
      <ModalContent>
        <button
          onClick={onClose}
          style={{
            position: "absolute",
            top: "10px",
            right: "10px",
            backgroundColor: "white",
            fontSize: "1.1rem",
            cursor: "pointer",
          }}
        >
          X
        </button>
        <ModalTitle>로그인 하여 참여하세요!</ModalTitle>
        <div
          style={{
            marginTop: "10%",
          }}
        >
          <Hr
            style={{
              backgroundColor: `#f1f3f5`,
              height: "2px",
              width: "100%",
            }}
          />
          <Span>소셜 로그인</Span>
          <a href="http://localhost:8080/login/oauth2/code/google">
            <LoginDiv />
          </a>
          <br />
          <a href="http://localhost:8080/login/oauth2/code/naver">
            <LoginDiv naver="true" />
          </a>
        </div>
      </ModalContent>
    </ModalOverlay>
  );
};
export default LoginModal;

const Span = styled.span`
  background-color: white;
  color: #abb0b5;
  z-index: 1;
  margin-bottom: 30px;
  display: inline-block;
  padding: 0 8px;
  position: relative;
  font-family: "notoBold";
  font-size: 0.8rem;
`;
const Hr = styled.hr`
  background-color: rgb(241, 243, 245);
  height: 2px;
  width: 100%;
  position: relative;
  top: 10px;
  display: block;
`;
