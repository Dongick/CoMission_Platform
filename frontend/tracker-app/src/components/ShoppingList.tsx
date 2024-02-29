import Item from "./item";
interface ShoppingListProps {
  items: Item[];
}
const ShoppingList = (props: ShoppingListProps) => {
  return (
    <div>
      <h1>Shopping List</h1>
      <ul>
        {props.items.map((item) => (
          <li key={item.id}>
            {item.product} - {item.quantity}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ShoppingList;
