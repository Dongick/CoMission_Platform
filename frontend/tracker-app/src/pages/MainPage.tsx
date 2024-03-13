import { theme } from "../styles/theme";
import StyledButton from "../components/StyledButton";
const LoginPage = () => {
  const printMsg = () => {
    console.log("clicked!!");
  };
  return (
    <>
      <h1>This is MainPage</h1>
      <StyledButton bgcolor={theme.mainYellow} color="black" onClick={printMsg}>
        Click me
      </StyledButton>
      <a href="http://localhost:8080/login/oauth2/code/google">Google Login</a>
      <br />
      <a href="http://localhost:8080/login/oauth2/code/naver">Naver Login</a>
    </>
  );
};

export default LoginPage;
