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
    <aside>
      <h1>정렬 방식</h1>
      <div>
        <label>
          Sort By:
          <StyledSelect value={sortBy} onChange={handleSortChange}>
            <option value="recent">Most Recent</option>
            <option value="participants">Participants</option>
            {/* Add more sorting options here */}
          </StyledSelect>
        </label>
      </div>
      <h1>미션 필터</h1>
      <div>
        <label>
          <StyledCheckbox
            type="checkbox"
            name="started"
            checked={filters.started}
            onChange={handleFilterChange}
          />
          Started Missions
        </label>
        <label>
          <StyledCheckbox
            type="checkbox"
            name="notStarted"
            checked={filters.notStarted}
            onChange={handleFilterChange}
          />
          Not Started Missions
        </label>
      </div>
    </aside>
  );
};

const StyledSelect = styled.select``;

const StyledCheckbox = styled.input``;
export default Filter;
