import axios, {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from "axios";
import { APIResponse } from "./interfaces";

// Axios instance 생성
const apiRequester: AxiosInstance = axios.create({
  baseURL: "http://localhost:3000", // BASE URL
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
  //  image나 영상 같은 거를 formData로 보낼 때에 는 multipart/form-data를 메서드 정의할 때 header에 작성
});

// 요청 interceptor
apiRequester.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const accessToken = localStorage.getItem("accessToken");
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    if (config.data instanceof FormData) {
      config.headers["Content-Type"] = "multipart/form-data";
    }
    return config;
  },
  (error) => {
    console.log(`request interceptor 에러: ${error}`);
    return Promise.reject(error);
  }
);

// 응답 interceptor
apiRequester.interceptors.response.use(
  (response: AxiosResponse) => {
    //todo 응답 시 콜백함수 추가 가능
    return response;
  },
  (error) => {
    // 토큰 만료 확인
    console.log(`request failed: ${error.message}`);
    if (error.response?.status === 401) {
      // 예: refreshToken을 사용하여 새로운 accessToken을 요청
      try {
        // error.config.headers.Authorization = `Bearer ${refreshedAccessToken}`;
      } catch (refreshError) {
        console.error("Failed to refresh access token:", refreshError);
        //todo 로그아웃 시키고 메인 페이지 이동
      }
    }
    return Promise.reject(error);
  }
);

// GET Method
export const getData = async <T>(
  url: string,
  config?: AxiosRequestConfig
): Promise<T> => {
  try {
    const response = await apiRequester.get<T>(url, config);
    return response.data;
  } catch (error) {
    if (error instanceof Error)
      throw new Error(`Failed to get data from ${url}: ${error.message}`);
    throw new Error(`Failed to get data from ${url}: Unknown error occurred`);
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
    if (error instanceof Error)
      throw new Error(`Failed to post data from ${url}: ${error.message}`);
    throw new Error(`Failed to post data from ${url}: Unknown error occurred`);
  }
};
