import axios, {
  AxiosError,
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from "axios";
import { useSetRecoilState } from "recoil";
import { NavigateFunction, useNavigate } from "react-router";
import { userInfo } from "./recoil";
import { UserInfoType } from "./types";
// Axios instance 생성
const apiRequester: AxiosInstance = axios.create({
  baseURL: "http://localhost:8080", // BASE URL
  timeout: 5000,
});

// 요청 interceptor
apiRequester.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const accessToken = localStorage.getItem("accessToken");
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    // if (config.data instanceof FormData) {
    //   config.headers["Content-Type"] = "multipart/form-data";
    // }
    return config;
  },
  (error: AxiosError) => {
    console.log(`request interceptor 에러: ${error}`);
    return Promise.reject(error);
  }
);
// 응답 interceptor
apiRequester.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  async (error: AxiosError) => {
    console.log(`request failed(response body data): ${error.response?.data}`);
    // AccessToken 만료
    if (error.response?.status === 401) {
      try {
        const navigate = useNavigate();
        const setUserInfoState = useSetRecoilState(userInfo);
        const newAccessToken = await refreshAccessToken(
          navigate,
          setUserInfoState
        );
        localStorage.setItem("accessToken", newAccessToken as string);
        if (error.config) {
          error.config.headers.Authorization = `Bearer ${newAccessToken}`;
          return apiRequester.request(error.config);
        } else {
          console.error("Original request config is undefined.");
        }
      } catch (refreshError) {
        console.error(`액세스 토큰 갱신 에러: ${refreshError}`);
        throw refreshError;
      }
    }
    return Promise.reject(error);
  }
);

// 액세스토큰 재발급 함수
const refreshAccessToken = async (
  navigate: NavigateFunction,
  setUserInfoState: React.Dispatch<React.SetStateAction<UserInfoType>>
) => {
  try {
    const newAccessToken = await postData<string, string>("/api/reissue", "");
    return newAccessToken;
  } catch (error) {
    const axiosError = error as AxiosError;
    console.log(`refresh Token 만료 response, ${axiosError.response?.data}`);
    console.log(`refresh Token 만료 status, ${axiosError.response?.status}`);
    console.log(
      `refresh Token 만료 statusText, ${axiosError.response?.statusText}`
    );
    if (axiosError.response?.status === 400) {
      throw new Error(`Failed to get new AccessToken: ${axiosError.message}`);
    }
    // refresh token도 만료일 때
    else if (axiosError.response?.status === 401) {
      postData<string, string>("/api/user/logout", "")
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
          alert(`로그아웃 에러,${error}`);
          throw error;
        });
    }
  }
};

// GET Method
export const getData = async <T>(
  url: string,
  config?: AxiosRequestConfig
): Promise<T> => {
  try {
    const response = await apiRequester.get<T>(url, config);
    return response.data;
  } catch (error) {
    console.error(`getData 에러 발생: ${error}`);
    throw error;
    // if (error instanceof Error)
    //   throw new Error(`Failed to get data from ${url}: ${error.message}`);
    // throw new Error(`Failed to get data from ${url}: Unknown error occurred`);
  }
};

// POST Method
export const postData = async <T, R>(
  url: string,
  data: T,
  config?: AxiosRequestConfig
): Promise<R> => {
  try {
    const response = await apiRequester.post<R>(url, data, config);
    return response.data;
  } catch (error) {
    console.error(`postData 에러 발생: ${error}`);
    throw error;
    // if (error instanceof Error)
    //   throw new Error(`Failed to post data from ${url}: ${error.message}`);
    // throw new Error(`Failed to post data from ${url}: Unknown error occurred`);
  }
};
