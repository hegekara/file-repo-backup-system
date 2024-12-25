import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import App from "./App";
import "./index.css"
import Login from "./pages/Login";
import Register from "./pages/Register";
import Home from "./pages/Home"
import NoPage from "./pages/NoPage";
import AdminLogin from "./admin-panel/AdminLogin";
import AdminPanel from "./admin-panel/AdminPanel";
import Repo from "./pages/Repo";

ReactDOM.createRoot(document.getElementById("root")).render(
  <>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} /> 
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/home" element={<Home />} />
        <Route path="/my-repo" element={<Repo />} />

        <Route path="/admin-login" element={<AdminLogin />} />
        <Route path="/admin-panel" element={<AdminPanel />} />

        <Route path="/*" element={<NoPage/>}/>
      </Routes>
    </BrowserRouter>
  </>
);