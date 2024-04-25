import styled from "styled-components";
import upArrow from "../assets/img/up-arrow.png";
const MoveToTop = () => {
  const moveToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };
  return <MoveToTopBtn onClick={moveToTop}></MoveToTopBtn>;
};

export default MoveToTop;

const MoveToTopBtn = styled.div`
  background-image: url(${upArrow});
  background-position: center;
  background-size: cover;
  height: 30px;
  width: 30px;
  position: fixed;
  bottom: 10vh;
  right: 5vw;
  z-index: 99;
  border: 1px solid black;
  border-radius: 3px;
  cursor: pointer;
  background-color: transparent;
`;
