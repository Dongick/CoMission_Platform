import axios, {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from "axios";

// Axios instance 생성
export const customAxios: AxiosInstance = axios.create({
  baseURL: "http://localhost:8080", // BASE URL
  timeout: 5000,
  withCredentials: true,
});

// 요청 interceptor
customAxios.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const accessToken = localStorage.getItem("accessToken");
    console.log("요청 Config: ", config);
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    if (config.data instanceof FormData) {
      config.headers["Content-Type"] = "multipart/form-data";
    } else {
      config.headers["Content-Type"] = "application/json";
    }
    return config;
  },
  (error) => {
    console.log(`request interceptor 에러: ${error}`);
    return Promise.reject(error);
  }
);

//응답 interceptor
customAxios.interceptors.response.use(
  (response: AxiosResponse) => {
    console.log("응답 Config: ", response.config);
    return response;
  },
  async (error) => {
    const errorStatus = error.response?.status;
    const requestUrl = error.config.url;
    // reissue가 실패한 경우
    if (requestUrl === "/api/reissue") {
      console.log("재발급실패", errorStatus);
      // string을 반환
      return Promise.reject(error.config.url);
    }
    // 재발급 요청을 해야하는 경우
    else if (errorStatus === 401) {
      console.log("재발급해야됨", requestUrl, errorStatus);
      try {
        console.log("여기서 액세스토큰 재발급 요청");
        const newAccessToken = await refreshAccessToken();
        console.log("재발급 successful: ", newAccessToken);
        if (error.config) {
          error.config.headers.Authorization = `Bearer ${newAccessToken}`;
          console.log("Retry request headers: ", error.config.headers);
          return customAxios.request(error.config);
        }
      } catch (error) {
        return Promise.reject(error);
      }
    }
    return Promise.reject(error);
  }
);

export const refreshAccessToken = async () => {
  try {
    console.log("재발급받으러 왔어요");
    const newAccessToken = await postData<string, string>("/api/reissue", "");
    console.log("재발급성공: ", newAccessToken);
    return newAccessToken;
  } catch (error) {
    if (typeof error === "string") {
      throw error;
    }
    throw error;
  }
};
// GET Method
export const getData = async <T>(
  url: string,
  config?: AxiosRequestConfig
): Promise<T> => {
  try {
    const response = await customAxios.get<T>(url, config);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// POST Method
export const postData = async <T, R>(
  url: string,
  data: T,
  config?: AxiosRequestConfig
): Promise<R> => {
  try {
    const response = await customAxios.post<R>(url, data, config);
    return response.data;
  } catch (error) {
    throw error;
  }
};
