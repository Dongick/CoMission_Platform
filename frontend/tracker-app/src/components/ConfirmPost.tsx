import styled from "styled-components";
import { useState, useEffect } from "react";
import { ConfirmPostIndexType } from "../types";
import { theme } from "../styles/theme";
import deleteImg from "../assets/img/delete.png";
import editImg from "../assets/img/edit.png";
import { useParams } from "react-router-dom";
import { deleteData } from "../axios";
import { useQueryClient } from "@tanstack/react-query";
import PostEditModal from "./PostEditModal";

type ModalHandlerType = {
  id: string;
} & ConfirmPostIndexType;

const ConfirmPost = ({ post, index, id }: ModalHandlerType) => {
  const [isEditClicked, setIsEditClicked] = useState<boolean>(false);
  const queryClient = useQueryClient();
  const { cardId } = useParams();
  const [canEdit, setCanEdit] = useState<boolean>(false);
  useEffect(() => {
    checkIfWithin24Hours(post.date);
  }, [post]);

  const checkIfWithin24Hours = (postDateIso: string) => {
    const postDate = new Date(postDateIso);
    const currentDate = new Date();
    // 밀리초 단위로 차이 계산
    const timeDifference = currentDate.getTime() - postDate.getTime();
    // 밀리초를 시간으로 변환
    const hoursDifference = timeDifference / (1000 * 60 * 60);
    setCanEdit(hoursDifference <= 24);
  };
  const convertIsoDate = (postDateIso: string) => {
    const [datePart, timePart] = postDateIso.split("T");
    const formattedTimePart = timePart.replace("Z", "");
    return `${datePart} ${formattedTimePart}`;
  };
  const closeModal = () => {
    setIsEditClicked(false);
  };
  const editHandler = () => {
    setIsEditClicked(true);
  };
  const deleteHandler = () => {
    const isConfirmed = window.confirm("글을 삭제하시겠습니까?");
    if (isConfirmed) {
      deleteData(`/api/authentication/${cardId}`)
        .then(() => {
          queryClient.invalidateQueries({ queryKey: ["authenticationData"] });
        })
        .catch((error) => alert(error));
    }
  };
  return (
    <PostLayout>
      <PostHeader>
        {canEdit && (
          <IconWrapper>
            <img
              src={editImg}
              alt="edit"
              width="20px"
              height="20px"
              onClick={editHandler}
            />
            <img
              src={deleteImg}
              alt="delete"
              width="20px"
              height="20px"
              onClick={deleteHandler}
            />
          </IconWrapper>
        )}
        <p
          style={{
            fontSize: "1.1rem",
            fontFamily: "notoBold",
          }}
        >
          {post.username}
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
          {convertIsoDate(post.date)}
        </p>
      </PostHeader>
      <PostContent>
        <p>{post.textData}</p>
        {post.photoData && (
          <img src={post.photoData} alt={`Post ${index}`} width="100%" />
        )}
      </PostContent>
      {isEditClicked && (
        <PostEditModal
          onClose={closeModal}
          id={id}
          editPost={{ editText: post.textData }}
        />
      )}
    </PostLayout>
  );
};

const PostLayout = styled.section`
  position: relative;
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

const IconWrapper = styled.div`
  position: absolute;
  top: 10px;
  right: 10px;
  width: 50px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  & > img {
    opacity: 0.7;
    cursor: pointer;
    &:hover {
      opacity: 1;
    }
  }
`;

export default ConfirmPost;
