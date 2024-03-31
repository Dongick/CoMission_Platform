import styled from "styled-components";
import { theme } from "../styles/theme";
import { ModalOverlay, ModalContent } from "./StyledModal";
import { useCallback, useState } from "react";
import StyledButton from "./StyledButton";
import { postData } from "../axios";
import { useQueryClient } from "@tanstack/react-query";
interface NewPostModalProps {
  onClose: () => void;
  id: string;
}
const NewPostModal = ({ onClose, id }: NewPostModalProps) => {
  const queryClient = useQueryClient();
  const [text, setText] = useState<string>("");
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
      await postData(`/api/authentication/${id}`, formData);
      await queryClient.invalidateQueries({ queryKey: ["authenticationData"] });
      onClose();
    } catch (error) {
      console.error(error);
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
        <ModalTitle>인증 글 작성</ModalTitle>
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
          <StyledButton
            type="button"
            bgcolor={theme.subGreen}
            onClick={formSubmitHandler}
          >
            게시
          </StyledButton>
        </FormWrapper>
      </ModalContent2>
    </ModalOverlay>
  );
};

export default NewPostModal;

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
