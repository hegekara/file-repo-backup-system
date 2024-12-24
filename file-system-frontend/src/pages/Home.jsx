import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/Home.css";
import "../styles/Auth.css"
import Header from "../components/Header";

const Home = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [role] = useState(localStorage.getItem("role"));
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    setIsLoggedIn(!!token);

    if((role=="role_admin")){
      navigate("/admin-panel")
    }
  }, []);

  return (

    <div className="homepage-img">
      <Header isLoggedIn={isLoggedIn} />
      <img src="../src/static/images/homepage-img.jpg" alt="Hotel Image" />
      <div className="content">
        <h1>Welcome to File Repo App</h1>
        {!isLoggedIn && (
          <div className="auth-buttons">
            <Link to="/login" className="home-button">Login</Link>
            <Link to="/register" className="home-button">Register</Link><br /><br /><br />
            <Link to="/admin-login" className="home-button">Admin Login</Link>
          </div>
        )}
        {isLoggedIn && (
          <div>
            <p className="desc">Backup and share files with your friends .</p>
            <Link to="/my-repo" className="home-button">My Repository</Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default Home;