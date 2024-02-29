import React, { useState } from "react";
import "./App.css";
import Greeter from "./components/Greeter";
import ShoppingList from "./components/ShoppingList";
import Item from "./components/item";

function App() {
  const [items, setItems] = useState<Item[]>([]);

  return (
    <div className="App">
      <ShoppingList items={items} />
      <a href="http://localhost:8080/login/oauth2/code/google">Google Login</a>
      <br />
      <a href="http://localhost:8080/login/oauth2/code/naver">Naver Login</a>
    </div>
  );
}

export default App;
