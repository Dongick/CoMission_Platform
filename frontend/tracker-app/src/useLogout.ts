import { userInfo } from "./recoil";
import { useSetRecoilState } from "recoil";
import { useNavigate } from "react-router";

const useLogout = () => {
  const navigate = useNavigate();
  const setUserInfo = useSetRecoilState(userInfo);
  const logout = (): void => {
    localStorage.removeItem("accessToken");
    setUserInfo({
      isLoggedIn: false,
      user_id: "",
      user_email: "",
    });
    navigate("/");
  };

  return logout;
};

export default useLogout;
