import styled from "styled-components";
import { ConfirmPostDataType } from "../types";
import { useQuery } from "@tanstack/react-query";
import { getData } from "../axios";
import ConfirmPost from "./ConfirmPost";
import { NoLoginContent } from "../pages/mission/MissionConfirmPostPage";
import { AxiosError } from "axios";
import { ErrorResponseDataType } from "../types";
const PostListLayout = styled.div``;
interface ConfirmPostListProps {
  id: string;
}
const ConfirmPostList = ({ id }: ConfirmPostListProps) => {
  const { data, isLoading, isError, error } = useQuery({
    queryKey: ["authenticationData"],
    queryFn: () => getData<ConfirmPostDataType[]>(`/api/authentication/${id}`),
  });
  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    const axiosError = error as AxiosError<ErrorResponseDataType>;
    const errorCode = axiosError.response?.data.errorCode;
    console.error(`인증 글 로드 실패: ${axiosError}`);
    if (errorCode === "UNAUTHORIZED") {
      return (
        <NoLoginContent>
          <span>❌</span>
          <h1>토큰이 존재하지 않습니다.</h1>
          <p>로그인을 해주세요</p>
        </NoLoginContent>
      );
    }
    if (axiosError.status === 400) {
      return (
        <NoLoginContent>
          <span>❌</span>
          <h1>아직 시작되지 않은 미션입니다.</h1>
          <p>미션 멤버가 부족합니다.</p>
        </NoLoginContent>
      );
    }
    if (axiosError.status === 404) {
      return (
        <NoLoginContent>
          <span>❌</span>
          <h1>해당 미션의 멤버가 아닙니다</h1>
          <p>미션에 참가해보세요</p>
        </NoLoginContent>
      );
    }
  }

  return (
    <PostListLayout>
      {data?.map((post, index) => (
        <ConfirmPost index={index + 1} post={post} />
      ))}
    </PostListLayout>
  );
};

export default ConfirmPostList;
