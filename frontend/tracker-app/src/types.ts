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
export type MissionType = Partial<{
  title: string;
  creatorEmail: string;
  description: string;
  created: Date;
  start: Date;
  deadline: Date;
  minParticipants: number;
  participants: number;
  duration: number;
  status: string;
  frequency: string;
  participant: boolean;
}>;

export type MissionConfirmPostType = {
  //todo 글쓴이가 추가되어야 함.
  date: Date;
  completed: boolean;
  photo: string;
  text: string;
};
export type ConfirmPostListType = {
  postList: MissionConfirmPostType[];
};

export type ConfirmPostType = {
  post: MissionConfirmPostType;
  index: number;
};

export type UserInfoType = {
  user_id: string;
  user_email: string;
  isLoggedIn: boolean;
};
