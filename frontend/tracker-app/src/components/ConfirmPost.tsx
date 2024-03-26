import styled from "styled-components";
import { useState } from "react";
import { ConfirmPostType } from "../types";
import exampleImg from "../assets/img/roadmap-77.png";
import { theme } from "../styles/theme";
import PostModal from "./PostModal";
const ConfirmPost = ({ post, index }: ConfirmPostType) => {
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
          글쓴 사람
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
        <p>
          {post.text} 스프링(Spring Fram ework)을 완전히 마스터할 수 있는 학습
          로드맵 입니다. 막 자바 학습을 끝낸 분들, 서버 개발자로 취업을 준비하는
          분들은 물론 이미 현업 에서 스프링을 사용하며 수준을 한 단계 끌어올리고
          싶은 분들까지 모두 에게 도움이 됩니다. 원리부터 응용까지, 내공있는
          백엔드 개발자로 성 장할 수 있도록 스프링을 제대로 이해하고 사용하는
          방법을 알려드립니다.
        </p>
        <img src={exampleImg} alt={`Post ${index}`} width="100%" />
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
