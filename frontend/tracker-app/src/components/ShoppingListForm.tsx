import { useRef } from "react";

interface ShoppingListFormProps {
  onAddItem: (item: string) => void;
}
const ShoppingListForm = ({ onAddItem }: ShoppingListFormProps) => {
  const productInputRef = useRef<HTMLInputElement>(null);
  const quantityInputRef = useRef<HTMLInputElement>(null);

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    onAddItem(productInputRef.current!.value);
    productInputRef.current!.value = "";
  };
  return (
    <form onSubmit={handleSubmit}>
      <input type="text" placeholder="Product Name" ref={productInputRef} />
      <input type="number" min={0} ref={quantityInputRef} />
      <button type="submit">Add Item</button>
    </form>
  );
};
export default ShoppingListForm;
