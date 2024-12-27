import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/Home.css";
import "../styles/Auth.css";
import Header from "../components/Header";
import API from '../api';

const Home = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [role] = useState(localStorage.getItem("role"));
  const [id] = useState(localStorage.getItem("id"));
  const [status, setStatus] = useState("");
  const navigate = useNavigate();
  const [error, setError] = useState("");

  const handleCheckPasswordStatus = async () => {
    try {
      const response = await API.get(`/user/check-password-status/${id}`);
      if (response && response.data) {
        setStatus(response.data.status);

        if (response.data.status === "ACCEPTED") {
          navigate("/change-password");
        }
      }
    } catch (err) {
      console.error("Error fetching checkStatus:", err);
      setError("Failed to fetch user details. Please try again.");
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    setIsLoggedIn(!!token);

    if (token) {
      handleCheckPasswordStatus();
    }

    if (role === "role_admin") {
      navigate("/admin-panel");
    }
  }, [role, navigate]);

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
            <p className="desc">Backup and share files with your friends.</p>
            <Link to="/my-repo" className="home-button">My Repository</Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default Home;