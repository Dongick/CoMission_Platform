import React from "react";
import "./App.css";
import Greeter from "./components/Greeter";
import ShoppingList from "./components/ShoppingList";
function App() {
  const items = [
    { id: 1, product: "Lemon", quantity: 3 },
    { id: 2, product: "Apple", quantity: 2 },
  ];

  return (
    <div className="App">
      {/* <ShoppingList items={items} /> */}
      <a href="http://localhost:8080/login/oauth2/code/google">Google Login</a>
      <br />
      <a href="http://localhost:8080/login/oauth2/code/naver">Naver Login</a>
    </div>
  );
}

export default App;
