import styled from "styled-components";
import { ConfirmPostDataType } from "../types";
import { useQuery } from "@tanstack/react-query";
import { getData } from "../axios";
import ConfirmPost from "./ConfirmPost";
import { theme } from "../styles/theme";
import { AxiosError } from "axios";
const PostListLayout = styled.div``;

const ConfirmPostList = () => {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["ConfrimPostList"],
    queryFn: () => getData<ConfirmPostDataType[]>("/api/authentication/test/1"), //todo api요청 주소 변수로 바꿔야 함
  });
  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    return <div>Error fetching posts</div>;
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
