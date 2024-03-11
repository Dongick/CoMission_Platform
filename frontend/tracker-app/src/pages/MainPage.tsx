import { theme } from "../styles/theme";
import StyledButton from "../components/StyledButton";
const LoginPage = () => {
  return (
    <>
      <h1>This is MainPage</h1>
      <StyledButton bgColor={theme.mainBlue} color="white">
        기모띠 Click me
      </StyledButton>
      <StyledButton bgColor={theme.mainGray} color="black">
        Click me
      </StyledButton>
      <a href="http://localhost:8080/login/oauth2/code/google">Google Login</a>
      <br />
      <a href="http://localhost:8080/login/oauth2/code/naver">Naver Login</a>
    </>
  );
};

export default LoginPage;
