import axios, {
  Axios,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from "axios";
import { APIResponse } from "./interfaces";

// Axios instance 생성
const instance: Axios = axios.create({
  baseURL: "http://localhost:3000", // BASE URL
  headers: {
    "Content-Type": "application/json",
  },
  //  image나 영상 같은 거를 formData로 보낼 때에 는 multipart/form-data를 메서드 정의할 때 header에 작성
});

// 요청 interceptor
instance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const accessToken = localStorage.getItem("accessToken");
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    console.log(error);
    return Promise.reject(error);
  }
);

// 응답 interceptor
instance.interceptors.response.use(
  (response: AxiosResponse) => {
    //todo 응답 시 콜백함수 추가 가능
    return response;
  },
  (error) => {
    // 토큰 만료 확인
    if (error.response && error.response.status === 401) {
      // 여기서 토큰 갱신 등의 작업 수행 가능
      // 예: refreshToken을 사용하여 새로운 accessToken을 요청
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
    const response = await instance.get<T>(url, config);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// POST Method
export const postData = async <T>(
  url: string,
  data: T,
  config?: AxiosRequestConfig
): Promise<T> => {
  try {
    const response = await instance.post<T>(url, data, config);
    return response.data;
  } catch (error) {
    throw error;
  }
};
