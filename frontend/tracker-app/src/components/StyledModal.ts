import styled from "styled-components";

export const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const ModalContent = styled.div`
  z-index: 1.1;
  position: relative;
  background-color: white;
  padding: 20px;
  min-height: 50%;
  border-radius: 8px;
`;
