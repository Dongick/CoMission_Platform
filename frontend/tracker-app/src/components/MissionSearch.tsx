import sectionSVG from "../assets/img/wave-haikei.webp";
import styled from "styled-components";
import StyledButton from "./StyledButton";
import Input from "./StyledInput";
import { theme } from "../styles/theme";
import { useRef } from "react";
import { postData } from "../axios";
import { useMutation } from "@tanstack/react-query";
import { SearchedMissionInfoType } from "../types";

interface MissionSearchProps {
  updateData: (newData: SearchedMissionInfoType) => void;
}
const MissionSearch = ({ updateData }: MissionSearchProps) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const searchMutation = useMutation<SearchedMissionInfoType, unknown, string>({
    mutationFn: (inputValue) =>
      postData("/api/mission/search", { title: inputValue }),
    onSuccess: (newData) => {
      updateData(newData);
    },
  });
  const searchHandler = async () => {
    const inputValue = inputRef.current?.value;
    try {
      if (inputValue) {
        await searchMutation.mutateAsync(inputValue);
      } else {
        alert("검색어를 입력해주세요");
      }
    } catch (error) {
      window.alert(`검색 에러: , ${error}`);
    }
  };

  return (
    <SearchSection>
      <div style={{ padding: "10px" }}>미션을 검색해보세요!</div>
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "center",
          padding: "10px",
        }}
      >
        <Input
          ref={inputRef}
          type="text"
          placeholder="미션명 검색하기"
          size={25}
          onKeyDown={(e) => {
            if (e.key === "Enter") searchHandler();
          }}
        />
        <StyledButton
          bgcolor={theme.subGreen}
          color="white"
          style={{ fontSize: "medium" }}
          onClick={searchHandler}
        >
          검색
        </StyledButton>
      </div>
    </SearchSection>
  );
};

export default MissionSearch;

export const SearchSection = styled.section`
  background-image: url(${sectionSVG});
  background-size: cover;
  background-position: center;
  height: 20vh;
  padding: 10px;
  font-family: "gmarket2";
  font-size: 2rem;
  @media screen and (max-width: 768px) {
    font-size: 1.5rem;
  }
  color: #333;
  display: flex;
  flex-direction: column;
  justify-content: center;
`;
