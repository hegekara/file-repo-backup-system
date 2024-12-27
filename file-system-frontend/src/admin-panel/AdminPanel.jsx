import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/Home.css";
import "../styles/Auth.css"
import Header from "../components/Header";

const AdminPanel = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [role, setRole] = useState(localStorage.getItem("role") || " ");
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    const role = (localStorage.getItem("role")).toLowerCase();
    setIsLoggedIn(!!token);
    setRole(role);
  }, []);

  return (

    <div className="homepage-img">
      <Header isLoggedIn={isLoggedIn}/>
      <img src="../src/static/images/admin-background.jpg" alt="Hotel Image" />
      <div className="content">
        <h1>Welcome to Admin Panel</h1>
        {!isLoggedIn && (
          <div className="auth-buttons">
            <Link to="/personel-login" className="home-button">Personel Login</Link>
          </div>
        )}
        {isLoggedIn && (role === "role_admin") && (
          <div>
            <p className="desc">Users and files can be managed from the admin panel</p>
            <Link to="/manage-user" className="home-button">Manage Users</Link>
            <Link to="/manage-team" className="home-button">Manage Teams</Link>
            <Link to="/manage-password" className="home-button">Manage Passwords</Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminPanel;