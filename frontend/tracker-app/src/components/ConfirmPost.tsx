import styled from "styled-components";
import { useState } from "react";
import { ConfirmPostIndexType } from "../types";
import { theme } from "../styles/theme";
import PostModal from "./PostModal";
const ConfirmPost = ({ post, index }: ConfirmPostIndexType) => {
  const [clickPost, setClickPost] = useState<boolean>(false);
  const postClickHandler = () => {
    setClickPost(true);
  };
  const postCloseHandler = () => {
    setClickPost(false);
  };
  return (
    <PostLayout>
      {clickPost && <PostModal onClose={postCloseHandler} />}
      <PostHeader>
        <p
          style={{
            fontSize: "1.1rem",
            fontFamily: "notoBold",
          }}
        >
          {post.username}{" "}
          <span style={{ fontSize: "0.8rem", fontFamily: "noto" }}>
            ({post.userEmail})
          </span>
        </p>
        <p
          style={{
            fontFamily: "noto",
            fontSize: "0.8rem",
            color: `${theme.subGray}`,
          }}
        >
          {post.date}
        </p>
      </PostHeader>
      <PostContent onClick={postClickHandler}>
        <p>{post.textData}</p>
        {post.photoData && (
          <img src={post.photoData} alt={`Post ${index}`} width="100%" />
        )}
      </PostContent>
    </PostLayout>
  );
};

const PostLayout = styled.section`
  background-color: white;
  margin-bottom: 20px;
  box-shadow: ${theme.boxShadow};
`;

const PostHeader = styled.div`
  min-height: 50px;
  padding: 10px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  flex-direction: column;
  & > p {
    text-align: left;
    padding: 5px;
  }
`;

const PostContent = styled.div`
  cursor: pointer;
  min-height: 120px;
  padding: 10px;
  & > img {
    padding: 5px;
    margin: 0 auto;
  }
  & > p {
    padding: 10px;
  }
`;
export default ConfirmPost;
