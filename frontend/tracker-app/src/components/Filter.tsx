import styled from "styled-components";
import { theme } from "../styles/theme";
import { useLocation, useSearchParams } from "react-router-dom";
import { ChangeEvent, useCallback } from "react";
import { FilterType, MainServerResponseType } from "../types";
import { useSetRecoilState } from "recoil";
import { userInfo } from "../recoil";
import { QueryObserverResult } from "@tanstack/react-query";
interface FilterProps {
  sort: string;
  setSort: React.Dispatch<React.SetStateAction<string>>;
  filter: FilterType;
  setFilter: React.Dispatch<React.SetStateAction<FilterType>>;
  refetch: () => Promise<QueryObserverResult<MainServerResponseType, Error>>;
}

const Filter = ({ sort, filter, setSort, setFilter, refetch }: FilterProps) => {
  const location = useLocation();
  const setUserInfo = useSetRecoilState(userInfo);
  // 이미 URL에 query존재 시 기존 값으로 설정
  const [searchParams, setSearchParams] = useSearchParams();
  // sort = searchParams.get("sort") || "sibal";
  // console.log(sort);
  // searchParams.getAll("filter").forEach((value) => {
  //   if (value === "started" || value === "created") {
  //     filter[value] = true;
  //   }
  // });

  const sortChangeHandler = useCallback((e: ChangeEvent<HTMLSelectElement>) => {
    setSort(e.target.value);
  }, []);

  const filterChangeHandler = useCallback(
    (e: ChangeEvent<HTMLInputElement>) => {
      const { name, checked } = e.target;
      setFilter((prevFilter) => ({
        ...prevFilter,
        [name]: checked,
      }));
    },
    []
  );

  // 조건이 바뀌면, 쿼리 값 업데이트
  const handleApplyButtonClick = () => {
    refetch();
    updateQueryURLHandler();
  };

  const handleResetButtonClick = () => {
    setUserInfo((prev) => ({ ...prev, started: false, created: false }));
    window.location.href = "/";
  };

  const updateQueryURLHandler = () => {
    setUserInfo((prev) => ({
      ...prev,
      started: filter.started,
      created: filter.created,
    }));
    const newSearchParams = new URLSearchParams(location.search);
    if (sort) newSearchParams.set("sort", sort);
    if (filter.started) {
      newSearchParams.set("started", "true");
    } else {
      newSearchParams.delete("started");
    }

    if (filter.created) {
      newSearchParams.set("created", "true");
    } else {
      newSearchParams.delete("created");
    }
    setSearchParams(newSearchParams, { replace: true });
  };

  return (
    <Asidebar>
      <div>
        <label>
          <StyledSelect value={sort} onChange={sortChangeHandler}>
            <option value="latest">최신순</option>
            <option value="participants">참여자순</option>
          </StyledSelect>
        </label>
      </div>
      <FilterDiv>
        <div>미션 필터</div>
        <div>
          <label>
            <StyledCheckbox
              type="checkbox"
              name="started"
              checked={filter.started}
              onChange={filterChangeHandler}
            />
            시작된 미션
          </label>
          <label>
            <StyledCheckbox
              type="checkbox"
              name="created"
              checked={filter.created}
              onChange={filterChangeHandler}
            />
            시작되지 않은 미션
          </label>
        </div>
      </FilterDiv>
      <div className="ButtonWrapper">
        <button onClick={handleResetButtonClick}>초기화</button>
        <button onClick={handleApplyButtonClick}>적용</button>
      </div>
    </Asidebar>
  );
};

const Asidebar = styled.aside`
  min-width: 200px;
  height: max-content;
  position: sticky;
  top: 30px;
  padding: 10px;

  @media screen and (max-width: 1000px) {
    min-width: 100px;
  }
  @media screen and (max-width: 700px) {
    min-width: 50px;
  }
  .ButtonWrapper {
    display: flex;
    justify-content: space-evenly;

    & > button:first-child {
      background-color: ${theme.subGray};
    }
  }
  button {
    margin-top: 10px;
    width: 50px;
    height: 30px;
    cursor: pointer;
    border-radius: 3px;
    background-color: ${theme.subGreen};
    color: white;
    &:hover {
      box-shadow: 1px 1px 5px 2px rgba(0, 0, 0, 0.1);
    }
  }
`;
const StyledSelect = styled.select`
  width: 100%;
  height: 35px;
  margin: 30px 0px;
  border: 1px solid ${theme.subGray};
  border-radius: 3px;
  font-size: 1rem;
  font-family: noto;
  cursor: pointer;
`;

const FilterDiv = styled.div`
  display: flex;
  flex-direction: column;
  border: 2px solid #f5f5f5;
  border-radius: 3px;
  font-size: 1rem;
  font-family: noto;

  div:first-child {
    height: 50px;
    background-color: #f5f5f5;
    color: #595959;
    font-size: 1.1rem;
    font-weight: 800;
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }
  & > div:nth-child(2) {
    background-color: white;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    padding: 18px 24px;
    label {
      height: 30px;
      width: 100%;
      cursor: pointer;
      display: flex;
      justify-content: initial;
      align-items: center;
      white-space: nowrap;
      overflow: hidden;
    }
  }
`;
const StyledCheckbox = styled.input`
  margin-right: 5px;
`;
export default Filter;
