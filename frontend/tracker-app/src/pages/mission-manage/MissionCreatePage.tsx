import Layout from "../../layouts/Layout";
import styled from "styled-components";
import { SearchSection } from "../MainPage";
import { theme } from "../../styles/theme";
const MissionCreatePage = () => {
  return (
    <Layout>
      <div style={{ backgroundColor: `${theme.mainGray}` }}>
        <MissionCreateBanner>미션 생성페이지</MissionCreateBanner>
        <MissionFormWrapper>zz시발</MissionFormWrapper>
      </div>
    </Layout>
  );
};

export default MissionCreatePage;

const MissionCreateBanner = styled(SearchSection)`
  background: none;
  background-color: white;
  border: 2px solid ${theme.mainGray};
`;

const MissionFormWrapper = styled.section`
  background-color: white;
  min-height: 80vh;
  padding: 3vh;
  width: 50%;
  margin: 0 auto;
`;
