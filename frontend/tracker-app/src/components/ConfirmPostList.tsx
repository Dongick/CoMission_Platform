import styled from "styled-components";
import { ConfirmPostDataType } from "../types";
import { useQuery } from "@tanstack/react-query";
import { getData } from "../axios";
import ConfirmPost from "./ConfirmPost";
import { theme } from "../styles/theme";
const PostListLayout = styled.div``;

const ConfirmPostList = () => {
  const { data, isLoading, isError, error } = useQuery({
    queryKey: ["ConfrimPostList"],
    queryFn: () => getData<ConfirmPostDataType[]>("/api/authentication/test/1"),
  });
  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    return <div>Error fetching posts</div>;
  }
  console.log(data);
  console.log(error);
  return (
    <PostListLayout>
      {data?.map((post, index) => (
        <ConfirmPost index={index + 1} post={post} />
      ))}
    </PostListLayout>
  );
};

export default ConfirmPostList;
