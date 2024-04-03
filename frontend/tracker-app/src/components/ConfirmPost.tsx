import styled from "styled-components";
import React, { useState } from "react";
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

  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0");
  const day = String(today.getDate()).padStart(2, "0");
  const formattedToday = `${year}-${month}-${day}`;

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
        {formattedToday === post.date && (
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
          {post.date}
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
