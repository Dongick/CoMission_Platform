export type SimpleMissionInfoType = {
  title: string;
  minParticipants: number;
  participants: number;
  duration: number;
  status: string;
  frequency: string;
};

export type MainServerResponseType = {
  participantMissionInfoList: SimpleMissionInfoType[];
  missionInfoList: SimpleMissionInfoType[];
};

// 미션 상세 정보
export type MissionType = {
  title: string;
  creatorEmail?: string;
  description: string;
  created?: string;
  start?: string;
  deadline?: string;
  minParticipants: number;
  participants: number;
  duration: number;
  status?: string;
  frequency: string;
  participant: boolean;
};

export type ConfirmPostDataType = {
  author?: string;
  userEmail: string;
  date: string;
  photoData: string;
  textData: string;
};

export type ConfirmPostIndexType = {
  post: ConfirmPostDataType;
  index: number;
};

export type UserInfoType = {
  user_id: string;
  user_email: string;
  isLoggedIn: boolean;
};
