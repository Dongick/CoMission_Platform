import Layout from "../../layouts/Layout";
import styled from "styled-components";
import { SearchSection } from "../../components/MissionSearch";
import { theme } from "../../styles/theme";
import missionImg from "../../assets/img/mission-img.png";
import Form from "../../components/StyledForm";
import { useState, useCallback } from "react";
import Input from "../../components/StyledInput";
import StyledButton from "../../components/StyledButton";
import { putData } from "../../axios";
import { useNavigate, useParams } from "react-router-dom";
import { useQueryClient, useQuery } from "@tanstack/react-query";
import { MissionType } from "../../types";
const MissionEditPage = () => {
  const navigate = useNavigate();
  const { cardId } = useParams();
  const { data, isError } = useQuery<MissionType>({
    queryKey: ["missionDetailInfo", `${cardId}`],
  });

  const queryClient = useQueryClient();
  const [title, setTitle] = useState<string>(data ? data.title : "");
  const [description, setDescription] = useState<string>(
    data ? data.description : ""
  );
  const [photo, setPhoto] = useState<File | null>(null);
  const [minParticipants, setMinParticipants] = useState(
    data ? data.minParticipants : 2
  );
  const [frequency, setFrequency] = useState<string>(
    data ? data.frequency : "매일"
  );
  const [duration, setDuration] = useState<number>(data ? data.duration : 365);

  const titleChangeHandler = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      setTitle(e.target.value);
    },
    []
  );
  const descriptionChangeHandler = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      setDescription(e.target.value);
    },
    []
  );

  const photoChangeHandler = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      if (e.target.files && e.target.files.length > 0) {
        // If the user uploads a file, set it as the photo state
        setPhoto(e.target.files[0]);
      }
    },
    []
  );

  const minParticipantsChangeHandler = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      setMinParticipants(parseInt(e.target.value));
    },
    []
  );

  const frequencyChangeHandler = useCallback(
    (e: React.ChangeEvent<HTMLSelectElement>) => {
      setFrequency(e.target.value);
    },
    []
  );

  const durationChangeHandler = useCallback(
    (e: React.ChangeEvent<HTMLSelectElement>) => {
      setDuration(parseInt(e.target.value));
    },
    []
  );
  if (isError) {
    alert("미션 수정 취소!");
    navigate(`/mission/${cardId}/detail`);
  }

  const formSubmitHandler = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title || !description) {
      window.alert("모든 값을 입력해주세요!");
    }
    const formData = new FormData();
    const missionInfo = {
      afterTitle: title,
      description: description,
      minParticipants: minParticipants,
      duration: duration,
      frequency: frequency,
    };
    if (photo) {
      formData.append("photoData", photo);
    } else {
      formData.append("photoData", "");
    }
    formData.append(
      "missionInfo",
      new Blob([JSON.stringify(missionInfo)], { type: "application/json" })
    );
    try {
      await putData(`/api/mission/${cardId}`, formData);
      await queryClient.invalidateQueries({
        queryKey: ["missionDetailInfo", `${cardId}`],
      });
      navigate(`/mission/${cardId}/detail`);
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Layout footer={false}>
      <div
        style={{ backgroundColor: `${theme.mainGray}`, paddingBottom: "200px" }}
      >
        <MissionCreateBanner>
          <div>
            <h2>미션 공유 플랫폼이란?</h2>
            <p>
              미션 실천내용을 인증하며 같은 목표를 가지고 진행도를 공유합니다.
              <br />
              인증을 함께 할 최소 인원을 정하고, 멤버 모집 후 개설이 됩니다.
            </p>
          </div>
          <img src={missionImg} alt="zz" width={100} height={100} />
        </MissionCreateBanner>
        <MissionFormWrapper>
          <p>미션 상세 수정</p>
          <MissionFormView>
            <Form>
              <InputDiv>
                <label htmlFor="title">미션 제목 (필수)</label>
                <input
                  type="text"
                  id="title"
                  value={title}
                  placeholder="미션명(예: 매일 책 읽기)"
                  onChange={titleChangeHandler}
                  required
                  style={{
                    border: "1px solid #ebebeb",
                    marginTop: "15px",
                    width: "100%",
                    height: "30px",
                    textAlign: "left",
                    padding: "5px",
                  }}
                />
              </InputDiv>
              <InputDiv>
                <label htmlFor="description">미션 상세 설명 (필수)</label>
                <Input
                  id="description"
                  value={description}
                  onChange={descriptionChangeHandler}
                  required
                  placeholder="미션에 대한 설명과 인증 방법에 대해 설명해주세요"
                  type="textarea"
                  style={{ border: "1px solid #ebebeb", marginTop: "15px" }}
                />
              </InputDiv>
              <InputDiv>
                <label htmlFor="photo">미션 대표 이미지 </label>
                <input
                  type="file"
                  id="photo"
                  onChange={photoChangeHandler}
                  accept="image/*"
                  style={{ marginTop: "15px" }}
                />
              </InputDiv>
              <InputDiv>
                <label htmlFor="participants">미션 최소 인원 설정</label>
                <input
                  type="number"
                  id="participants"
                  value={minParticipants}
                  onChange={minParticipantsChangeHandler}
                  min={2}
                  required
                  style={{
                    border: "1px solid #ebebeb",
                    marginTop: "15px",
                    width: "20%",
                    height: "30px",
                    padding: "5px",
                  }}
                />
              </InputDiv>
            </Form>
          </MissionFormView>
          <p>미션 기간과 인증 빈도 수정</p>
          <MissionFormView>
            <Form>
              <InputDiv>
                <label htmlFor="frequency">미션 인증 주기</label>
                <select
                  id="frequency"
                  value={frequency}
                  onChange={frequencyChangeHandler}
                  style={{
                    border: "1px solid #ebebeb",
                    marginTop: "15px",
                    width: "30%",
                    height: "30px",
                    textAlign: "center",
                    padding: "5px",
                  }}
                >
                  <option value="매일">Every day</option>
                  <option value="주1회">주 1회</option>
                  <option value="주2회">주 2회</option>
                  <option value="주3회">주 3회</option>
                  <option value="주4회">주 4회</option>
                  <option value="주5회">주 5회</option>
                  <option value="주6회">주 6회</option>
                </select>
              </InputDiv>
              <InputDiv>
                <label htmlFor="duration">미션 참여 기간</label>
                <select
                  id="duration"
                  value={duration}
                  style={{
                    border: "1px solid #ebebeb",
                    marginTop: "15px",
                    width: "30%",
                    height: "30px",
                    textAlign: "center",
                    padding: "5px",
                  }}
                  onChange={durationChangeHandler}
                >
                  <option value={100} disabled>
                    100일
                  </option>
                  <option value={365}>365일</option>
                </select>
              </InputDiv>
            </Form>
          </MissionFormView>
        </MissionFormWrapper>
        <StyledButton
          type="button"
          style={{ marginRight: "10px" }}
          bgcolor={theme.subGray}
          color={theme.mainGray}
          onClick={() => {
            window.history.back();
          }}
        >
          취소
        </StyledButton>
        <StyledButton
          type="button"
          bgcolor={theme.subGreen}
          onClick={formSubmitHandler}
        >
          완료
        </StyledButton>
      </div>
    </Layout>
  );
};

export default MissionEditPage;

const MissionCreateBanner = styled(SearchSection)`
  background: none;
  background-color: white;
  border: 2px solid ${theme.mainGray};
  line-height: 2rem;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  & > div > h2 {
    font-family: "gmarket2";
    font-size: 1.5rem;
    text-align: left;
    padding: 5px;
  }
  & > div > p {
    font-family: "gmarket1";
    font-size: 1.2rem;
    text-align: left;
    padding: 5px;
  }
`;

export const MissionFormWrapper = styled.section`
  min-height: 100vh;
  padding: 3vh;
  width: 50%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  & > p {
    font-family: "notoBold";
    font-size: 1.1rem;
    margin: 10px;
  }
`;

export const MissionFormView = styled.div`
  width: 100%;
  min-height: 250px;
  background-color: white;
  margin-bottom: 50px;
  border-radius: 10px;
  font-family: "gmarket2  ";
`;

export const InputDiv = styled.div`
  padding: 20px;
  width: 100%;
  text-align: left;
  border-bottom: 2px solid ${theme.mainGray};
  flex-direction: column;
  align-items: flex-start;
  display: flex;
`;
