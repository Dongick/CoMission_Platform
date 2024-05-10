import styled from "styled-components";
import { theme } from "../styles/theme";
import { useNavigate, useLocation, useSearchParams } from "react-router-dom";
import { useEffect } from "react";

// 정렬: 최신순, 참가자 순
// 필터: 시작된 미션, 시작안된 미션

const Filter = () => {
  const location = useLocation();

  // 이미 URL에 query존재 시 기존 값으로 설정
  const [searchParams, setSearchParams] = useSearchParams();
  const sort = searchParams.get("sort") || "";
  const filter = searchParams.get("filter") || "";

  // 상태를 URLSearchParams 객체로 업데이트
  useEffect(() => {
    const newSearchParams = new URLSearchParams(location.search);
    if (sort) newSearchParams.set("sort", sort);
    if (filter) newSearchParams.set("filter", filter);
    setSearchParams(newSearchParams, { replace: true });
  }, [sort, filter]);

  return (
    <Asidebar>
      <div>
        <label>
          <StyledSelect>
            {/* value={sortBy} onChange={handleSortChange} */}
            <option value="recent">최신순</option>
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
              // checked={filters.started}
              // onChange={handleFilterChange}
            />
            Started Missions
          </label>
          <label>
            <StyledCheckbox
              type="checkbox"
              name="notStarted"
              // checked={filters.notStarted}
              // onChange={handleFilterChange}
            />
            Not Started Missions
          </label>
        </div>
      </FilterDiv>
    </Asidebar>
  );
};

const Asidebar = styled.aside`
  min-width: 200px;
  height: max-content;
  padding: 10px;
  background-color: ${theme.mainGray};
  @media screen and (max-width: 1000px) {
    min-width: 100px;
  }
  @media screen and (max-width: 700px) {
    min-width: 50px;
  }
`;
const StyledSelect = styled.select`
  width: 80%;
  height: 35px;
  margin: 30px 10px 30px 10px;
  border: 1px solid ${theme.subGray};
  border-radius: 3px;
  font-size: 1rem;
  font-family: noto;
  cursor: pointer;
`;

const FilterDiv = styled.div`
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
  border-radius: 3px;
  font-size: 1rem;
  font-family: noto;
  cursor: pointer;

  & > div:first-child {
    height: 30px;
    background-color: white;
    color: #595959;
    font-weight: 700;
  }
`;
const StyledCheckbox = styled.input``;
export default Filter;
