// 미션
// ObjectId id;
// String title;  미션 제목
// String description;  미션 설명
// LocalDateTime createdAt;  미션 생성일
// LocalDate startDate;  미션 시작일
// LocalDate deadline;  미션 마감일
// int minParticipants;  ex 최소 참가 인원수
// int participants;  미션 참가 인원수
// int duration;   ex 365일
// String status;   ex 미션 상태  CREATED, STARTED, COMPLETED
// String frequency; 인증 주기 ex daily
// String creatorEmail;  ex 미션 생성한 사용자
export type MissionType = {
  title: string;
  description: string;
  created: Date;
  start: Date;
  deadline: Date;
  minParticipants: number;
  participants: number;
  duration: number | string;
  status: string;
  frequency: string;
  creatorEmail: string;
};

// 인증글
// LocalDate date;
// boolean completed;
// String photoData;
// String textData;
export type MissionConfirmPost = {
  date: Date;
  completed: boolean;
  photo: string;
  text: string;
};

export type UserInfoType = {
  user_id: string;
  user_email: string;
  isLoggedIn: boolean;
};
