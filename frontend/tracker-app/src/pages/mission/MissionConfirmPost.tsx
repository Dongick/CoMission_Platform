import Layout from "../../layouts/Layout";
import { BannerSection, Navbar, NavButton, MainSection } from "./MissionStyles";
import { Link, useParams, useLocation } from "react-router-dom";
const MissionConfirmPost = () => {
  const { cardId } = useParams();
  const location = useLocation();
  const detailURL = `/mission/${cardId}/detail`;
  const confirmURL = `/mission/${cardId}/confirm-post`;
  return (
    <Layout>
      <BannerSection>zz</BannerSection>
      <Navbar>
        <Link to={detailURL}>
          <NavButton clicked={false}>미션 소개</NavButton>
        </Link>
        <Link to={confirmURL}>
          <NavButton clicked={true}>미션 인증글</NavButton>
        </Link>
      </Navbar>
      <MainSection>
        {cardId}
        <br />
        {location.pathname}
      </MainSection>
    </Layout>
  );
};

export default MissionConfirmPost;
