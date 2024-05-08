import styled from "styled-components";
import { theme } from "../styles/theme";
import { useNavigate, useLocation } from "react-router-dom";

// 정렬: 최신순, 참가자 순
// 필터: 시작된 미션, 시작안된 미션

const Filter = () => {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);

  return (
    <div>
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
    </div>
  );
};

const StyledSelect = styled.select``;

const StyledCheckbox = styled.input``;
export default Filter;
