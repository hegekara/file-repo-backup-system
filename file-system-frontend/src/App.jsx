import React from "react";
import { Outlet } from "react-router-dom";

const App = () => {

  return (
    <div>
      <h1>Hotel Reservation System</h1>
      <Outlet />
    </div>
  );
};

export default App;