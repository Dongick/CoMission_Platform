import { atom } from "recoil";
import { recoilPersist } from "recoil-persist";
import { UserInfoType } from "./types";

const { persistAtom } = recoilPersist({
  key: "localStorage", //원하는 key 값 입력
  storage: localStorage,
});

export const userInfo = atom<UserInfoType>({
  key: "userInfo",
  default: {
    user_id: "",
    user_email: "",
  },
  effects_UNSTABLE: [persistAtom],
});
