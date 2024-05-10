import { atom } from "recoil";
import { recoilPersist } from "recoil-persist";
import { UserInfoType } from "./types";

const { persistAtom } = recoilPersist({
  key: "localStorage",
  storage: localStorage,
});

export const userInfo = atom<UserInfoType>({
  key: "userInfo",
  default: {
    isLoggedIn: false,
    user_id: "",
    user_email: "",
    sort: "",
    filter: {
      start: false,
      notStart: false,
    },
  },
  effects_UNSTABLE: [persistAtom],
});
