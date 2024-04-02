import styled from "styled-components";
import { ConfirmPostDataType } from "../types";
import { useQuery } from "@tanstack/react-query";
import { getData } from "../axios";
import ConfirmPost from "./ConfirmPost";
import { theme } from "../styles/theme";
import { NoLoginContent } from "../pages/mission/MissionConfirmPostPage";
import { AxiosError } from "axios";
import { ErrorResponseDataType, ConfirmPostListType } from "../types";
import StyledButton from "./StyledButton";
import { useEffect, useState } from "react";
import NewPostModal from "./NewPostModal";
const PostListLayout = styled.div`
  padding: 5px;
`;
interface ConfirmPostListProps {
  id: string;
}
const ConfirmPostList = ({ id }: ConfirmPostListProps) => {
  const [showPostModal, setShowPostModal] = useState<boolean>(false);
  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: ["authenticationData"],
    queryFn: () => getData<ConfirmPostListType>(`/api/authentication/${id}/0`),
  });
  useEffect(() => {
    refetch();
  }, [data]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    const axiosError = error as AxiosError<ErrorResponseDataType>;
    const errorCode = axiosError.response?.data.errorCode;
    if (errorCode === "UNAUTHORIZED") {
      return (
        <NoLoginContent>
          <span>❌</span>
          <h1>토큰이 존재하지 않습니다.</h1>
          <p>로그인을 해주세요</p>
        </NoLoginContent>
      );
    }
    if (errorCode === "MISSION_NOT_STARTED") {
      return (
        <NoLoginContent>
          <span>❌</span>
          <h1>아직 시작되지 않은 미션입니다.</h1>
          <p>미션 멤버가 부족합니다.</p>
        </NoLoginContent>
      );
    }
    if (errorCode === "PARTICIPANT_NOT_FOUND") {
      return (
        <NoLoginContent>
          <span>❌</span>
          <h1>해당 미션의 멤버가 아닙니다</h1>
          <p>미션에 참가해보세요</p>
        </NoLoginContent>
      );
    }
  }

  const openPostModalHandler = () => {
    setShowPostModal(true);
  };
  const closePostModalHandler = () => {
    setShowPostModal(false);
  };
  return (
    <PostListLayout>
      <StyledButton
        bgcolor={theme.subGreen}
        style={{
          fontSize: "large",
          borderRadius: "10px",
          marginBottom: "30px",
        }}
        onClick={openPostModalHandler}
      >
        인증 글 작성
      </StyledButton>

      {data?.authenticationData ? (
        data?.authenticationData.map((post, index) => (
          <ConfirmPost index={index + 1} post={post} key={index} />
        ))
      ) : (
        <NoLoginContent>
          <span>❌</span>
          <h1>등록된 인증글이 없습니다</h1>
          <p>첫 인증을 해보세요!</p>
        </NoLoginContent>
      )}
      {showPostModal && (
        <NewPostModal onClose={closePostModalHandler} id={id} />
      )}
    </PostListLayout>
  );
};

export default ConfirmPostList;
