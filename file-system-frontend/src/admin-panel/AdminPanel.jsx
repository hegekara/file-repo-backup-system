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
        {isLoggedIn && (role === "admin") && (
          <div>
            <p className="desc">Users and bookings can be managed from the admin panel</p>
            <Link to="/admin-list-room" className="home-button">Manage Room</Link>
            <Link to="/admin-list-booking" className="home-button">Manage Booking</Link>
            {(role === "admin") && (
              <>
                <Link to="/list-personel" className="home-button">Manage Personel</Link>
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminPanel;