import { ModalContent, ModalOverlay } from "./StyledModal";

interface PostModalProps {
  onClose: () => void;
}
const PostModal = ({ onClose }: PostModalProps) => {
  const overlayclickHandler = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <ModalOverlay onClick={overlayclickHandler}>
      <ModalContent>
        <button
          onClick={onClose}
          style={{
            position: "absolute",
            top: "10px",
            right: "10px",
            backgroundColor: "white",
            fontSize: "1.1rem",
            cursor: "pointer",
          }}
        >
          X
        </button>
      </ModalContent>
    </ModalOverlay>
  );
};

export default PostModal;
