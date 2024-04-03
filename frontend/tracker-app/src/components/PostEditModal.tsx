import styled from "styled-components";
import { theme } from "../styles/theme";
import { ModalOverlay, ModalContent } from "./StyledModal";
import { useCallback, useState } from "react";
import StyledButton from "./StyledButton";
import { postData, putData } from "../axios";
import { useQueryClient } from "@tanstack/react-query";
import { AxiosError } from "axios";
interface NewPostModalProps {
  onClose: () => void;
  id: string;
  editPost?: {
    editText: string;
  };
}
const PostEditModal = ({ onClose, id, editPost }: NewPostModalProps) => {
  const queryClient = useQueryClient();
  const [text, setText] = useState<string>(editPost ? editPost.editText : "");
  const [photo, setPhoto] = useState<File | null>(null);
  const handleOverlayClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };
  const textDataChangeHandler = useCallback(
    (e: React.ChangeEvent<HTMLTextAreaElement>) => {
      setText(e.target.value);
    },
    []
  );
  const photoChangeHandler = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      if (e.target.files && e.target.files.length > 0) {
        setPhoto(e.target.files[0]);
      }
    },
    []
  );
  const formSubmitHandler = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!text) {
      window.alert("내용을 입력해주세요!");
    }
    const formData = new FormData();
    const textData = {
      textData: text,
    };
    if (photo) {
      formData.append("photoData", photo);
    } else {
      formData.append("photoData", "");
    }
    formData.append(
      "textData",
      new Blob([JSON.stringify(textData)], { type: "application/json" })
    );
    try {
      if (editPost) {
        await putData(`/api/authentication/${id}`, formData);
        await queryClient.invalidateQueries({
          queryKey: ["authenticationData"],
        });
        onClose();
      } else {
        await postData(`/api/authentication/${id}`, formData);
        await queryClient.invalidateQueries({
          queryKey: ["authenticationData"],
        });
        onClose();
      }
    } catch (error) {
      if (error instanceof AxiosError) {
        alert(error?.response?.data?.errorCode);
      }
    }
  };
  return (
    <ModalOverlay onClick={handleOverlayClick}>
      <ModalContent2>
        <button
          onClick={onClose}
          style={{
            position: "absolute",
            top: "10px",
            right: "10px",
            backgroundColor: "white",
            fontSize: "1.1rem",
            cursor: "pointer",
          }}
        >
          X
        </button>
        {editPost ? (
          <ModalTitle>인증 글 수정</ModalTitle>
        ) : (
          <ModalTitle>인증 글 작성</ModalTitle>
        )}
        <Hr />
        <FormWrapper>
          <textarea
            id="text"
            required
            value={text}
            onChange={textDataChangeHandler}
            placeholder="인증 글 내용을 작성해주세요"
          />
          <Hr />
          <input
            type="file"
            id="photo"
            onChange={photoChangeHandler}
            accept="image/*"
          />
          <Hr />
          <p style={{ padding: "10px" }}>
            ❗ 글 작성 후, <span style={{ fontFamily: "notoBold" }}>1일</span>{" "}
            동안만 수정/삭제가 가능합니다
          </p>
          {editPost ? (
            <StyledButton
              type="button"
              bgcolor={theme.subGreen}
              onClick={formSubmitHandler}
            >
              수정
            </StyledButton>
          ) : (
            <StyledButton
              type="button"
              bgcolor={theme.subGreen}
              onClick={formSubmitHandler}
            >
              게시
            </StyledButton>
          )}
        </FormWrapper>
      </ModalContent2>
    </ModalOverlay>
  );
};

export default PostEditModal;

const ModalContent2 = styled(ModalContent)`
  min-width: 40%;
  min-height: 400px;
`;

const ModalTitle = styled.h1`
  font-size: 1.5rem;
  font-family: "notoBold";
  color: ${theme.mainBlue};
  padding: 10px;
`;

const Hr = styled.hr`
  background-color: rgb(241, 243, 245);
  height: 2px;
  width: 100%;
  display: block;
  margin: 10px;
`;

const FormWrapper = styled.div`
  min-height: 80%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  & > textarea {
    min-width: 100%;
    max-width: 1000px;
    min-height: 200px;
    max-height: 400px;
    padding: 5px;
    font-size: 1rem;
    font-family: "noto";
    text-align: left;
    &:focus-visible {
      outline: none;
    }
  }
`;
