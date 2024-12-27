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
import MyTeams from "./pages/MyTeams";
import TeamRepo from "./pages/TeamRepo";
import Profile from "./pages/Profile";
import ManageUser from "./admin-panel/ManageUser";
import UserDetails from "./admin-panel/UserDetails";
import ManageTeam from "./admin-panel/ManageTeam";
import TeamDetails from "./admin-panel/TeamDetails";

ReactDOM.createRoot(document.getElementById("root")).render(
  <>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} /> 
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/home" element={<Home />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/my-repo" element={<Repo />} />
        <Route path="/my-teams" element={<MyTeams />} />
        <Route path="/team-repo/:teamId" element={<TeamRepo />} />

        <Route path="/admin-login" element={<AdminLogin />} />
        <Route path="/admin-panel" element={<AdminPanel />} />
        <Route path="/manage-user" element={<ManageUser />} />
        <Route path="/user-details/:userId" element={<UserDetails />} />
        <Route path="/manage-team" element={<ManageTeam />} />
        <Route path="/team-details/:teamId" element={<TeamDetails />} />
        

        <Route path="/*" element={<NoPage/>}/>
      </Routes>
    </BrowserRouter>
  </>
);