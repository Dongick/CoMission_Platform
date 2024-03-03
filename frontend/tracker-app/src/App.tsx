import React, { useState } from "react";
import styled from "styled-components";
import GlobalStyle from "./styles/GlobalStyle";
import { ThemeProvider } from "styled-components";
import theme from "./styles/theme";
import "./App.css";
import Greeter from "./components/Greeter";
import ShoppingList from "./components/ShoppingList";
import Item from "./components/item";
import ShoppingListForm from "./components/ShoppingListForm";
import { v4 as uuid4 } from "uuid";

function App() {
  const [items, setItems] = useState<Item[]>([]);
  const AddItem = (product: string) => {
    setItems([...items, { id: uuid4(), product: "abc", quantity: 1 }]);
  };
  return (
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <div className="App">
        <ShoppingList items={items} />
        <ShoppingListForm onAddItem={AddItem} />
        <a href="http://localhost:8080/login/oauth2/code/google">
          Google Login
        </a>
        <br />
        <a href="http://localhost:8080/login/oauth2/code/naver">Naver Login</a>
      </div>
    </ThemeProvider>
  );
}

export default App;
