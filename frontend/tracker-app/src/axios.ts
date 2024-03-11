import axios, { Axios, AxiosRequestConfig, AxiosResponse } from "axios";
import { APIResponse } from "./interfaces";

// Axios instance 생성
const client: Axios = axios.create({
  baseURL: "http://localhost:3000", // BASE URL
  headers: {
    "Content-Type": "application/json",
  },
  //  image나 영상 같은 거를 formData로 보낼 때에 는 multipart/form-data를 메서드 정의할 때 header에 작성
});

//todo intercepter 추가 해야되는지 고민

// GET Method
export const getData = async <T>(
  url: string,
  config?: AxiosRequestConfig
): Promise<T> => {
  try {
    const response = await client.get<T>(url, config);
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
    const response = await client.post<T>(url, data, config);
    return response.data;
  } catch (error) {
    throw error;
  }
};
