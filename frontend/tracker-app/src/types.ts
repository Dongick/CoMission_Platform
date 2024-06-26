export type SimpleMissionInfoType = {
  id: string;
  username: string;
  title: string;
  minParticipants: number;
  participants: number;
  duration: number;
  status: string;
  frequency: string;
  photoUrl: string;
};

export type ParticipatedMissionType = {
  simpleMissionInfoList: {
    id: string;
    title: string;
    photoUrl: string;
  }[];
};

export type LazyMissionInfoListType = {
  missionInfoList: SimpleMissionInfoType[];
};
export type MainServerResponseType = {
  participantMissionInfoList: SimpleMissionInfoType[];
  missionInfoList: SimpleMissionInfoType[];
};

export type SearchedMissionInfoType = {
  missionInfoList: SimpleMissionInfoType[];
};
// 미션 상세 정보
export type MissionType = {
  id: string;
  username: string;
  title: string;
  creatorEmail: string;
  description: string;
  createdAt: string;
  startDate: string;
  deadline: string;
  photoUrl: string;
  minParticipants: number;
  participants: number;
  duration: number;
  status: string;
  frequency: string;
  participant: boolean;
};

export type ConfirmPostDataType = {
  username?: string;
  userEmail: string;
  date: string;
  photoData: string;
  textData: string;
};

export type ConfirmPostIndexType = {
  post: ConfirmPostDataType;
  index: number;
};

export type ConfirmPostListType = {
  authenticationData: ConfirmPostDataType[];
};

export type UserMissionPostListType = {
  userMissionPostList: {
    date: string;
    photoData: string;
    textData: string;
  }[];
};

export type UserInfoType = {
  user_id: string;
  user_email: string;
  isLoggedIn: boolean;
  sort: string;
  filter: FilterType;
};

export type FilterType = {
  started: boolean;
  created: boolean;
};

export type ErrorResponseDataType = {
  errorCode: string;
  errorMessage: string;
};
