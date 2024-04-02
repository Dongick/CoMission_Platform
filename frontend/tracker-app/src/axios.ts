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
    return Promise.reject(error);
  }
);

//응답 interceptor
customAxios.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  async (error) => {
    const errorStatus = error.response?.status;
    const requestUrl = error.config.url;
    // reissue가 실패한 경우
    if (requestUrl === "/api/reissue") {
      // string을 반환
      return Promise.reject(error.config.url);
    }
    // 재발급 요청을 해야하는 경우
    else if (errorStatus === 401) {
      try {
        const newAccessToken = await refreshAccessToken();
        if (error.config) {
          localStorage.setItem("accessToken", newAccessToken);
          // error.config.headers.Authorization = `Bearer ${newAccessToken}`;
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
    const newAccessToken = await postData<string, string>("/api/reissue", "");
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
    let returnData = response.data;
    if (url === "/api/reissue") {
      returnData = response.headers?.authorization;
    }
    return returnData;
  } catch (error) {
    throw error;
  }
};

// PUT Method
export const putData = async <T, R>(
  url: string,
  data: T,
  config?: AxiosRequestConfig
): Promise<R> => {
  try {
    const response = await customAxios.put<R>(url, data, config);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// DELETE Method
export const deleteData = async <T>(
  url: string,
  config?: AxiosRequestConfig
): Promise<T> => {
  try {
    const response = await customAxios.delete<T>(url, config);
    return response.data;
  } catch (error) {
    throw error;
  }
};
