import styled from "styled-components";
import { MissionConfirmPostType, ConfirmPostListType } from "../types";
import ConfirmPost from "./ConfirmPost";
import { theme } from "../styles/theme";
const PostListLayout = styled.div``;

const ConfirmPostList = ({ postList }: ConfirmPostListType) => {
  return (
    <PostListLayout>
      {postList.map((post, index) => (
        <ConfirmPost post={post} index={index} key={index} />
      ))}
    </PostListLayout>
  );
};

export default ConfirmPostList;
