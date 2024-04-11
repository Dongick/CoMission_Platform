import styled from "styled-components";
import { useQuery, useInfiniteQuery } from "@tanstack/react-query";
import { getData } from "../axios";
import ConfirmPost from "./ConfirmPost";
import { theme } from "../styles/theme";
import { NoLoginContent } from "../pages/mission/MissionConfirmPostPage";
import { AxiosError } from "axios";
import { ErrorResponseDataType, ConfirmPostListType } from "../types";
import StyledButton from "./StyledButton";
import { useEffect, useState } from "react";
import PostEditModal from "./PostEditModal";
const PostListLayout = styled.div`
  padding: 5px;
`;
interface ConfirmPostListProps {
  id: string;
}
const ConfirmPostList = ({ id }: ConfirmPostListProps) => {
  const [showPostModal, setShowPostModal] = useState<boolean>(false);
  const fetchLazyData = async ({ pageParam = 0 }) =>
    await getData<ConfirmPostListType>(
      `/api/authentication/${id}/${pageParam}`
    );
  const {
    data,
    isLoading,
    isFetchingNextPage,
    isError,
    error,
    hasNextPage,
    fetchNextPage,
  } = useInfiniteQuery({
    queryKey: ["authenticationData"],
    queryFn: fetchLazyData,
    initialPageParam: 0,
    getNextPageParam: (lastList, allLists) => {
      if (
        lastList.authenticationData &&
        lastList.authenticationData.length === 5
      ) {
        return allLists.length;
      } else {
        return undefined;
      }
    },
  });

  if (isLoading) {
    return (
      <NoLoginContent>
        <h1>데이터 로딩중...</h1>
      </NoLoginContent>
    );
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
      {data?.pages[0].authenticationData ? (
        data?.pages.map((page) =>
          page.authenticationData.map((post, index) => (
            <ConfirmPost index={index + 1} post={post} key={index} id={id} />
          ))
        )
      ) : (
        <NoLoginContent>
          <span>❌</span>
          <h1>등록된 인증글이 없습니다</h1>
          <p>첫 인증을 해보세요!</p>
        </NoLoginContent>
      )}
      {hasNextPage ? (
        <StyledButton
          onClick={() => {
            fetchNextPage();
          }}
          disabled={isFetchingNextPage}
          bgcolor={theme.subGreen}
          style={{ fontSize: "1.1rem" }}
        >
          {isFetchingNextPage ? "Loading..." : "Load More"}
        </StyledButton>
      ) : (
        <StyledButton
          disabled
          bgcolor={theme.mainGray}
          color={theme.subGray}
          style={{ fontSize: "1.1rem", boxShadow: "none", cursor: "auto" }}
        >
          더 이상 미션이 없습니다!
        </StyledButton>
      )}
      {showPostModal && (
        <PostEditModal onClose={closePostModalHandler} id={id} />
      )}
    </PostListLayout>
  );
};

export default ConfirmPostList;
