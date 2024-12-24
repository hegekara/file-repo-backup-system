import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import API from "../api";
import "../styles/Auth.css";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (localStorage.getItem("jwtToken")) {
      navigate("/home");
    }
  }, [navigate]);


  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await API.post("/user/login", {username, password});

      console.log("Response:", response.data);

      const token = response.data.token;
      const id = response.data.object.id;
      const role = (response.data.object.role).toLowerCase();

      if (token) {
        localStorage.setItem("jwtToken", token);
        localStorage.setItem("id", id);
        localStorage.setItem("role", role);
        navigate("/home");
      }
    } catch (err) {
      setError("Login failed. Please try again.");
    }
  };

  return (
    <div className="homepage-img">
      <img src="../src/static/images/homepage-img.jpg" alt="Hotel Image" />
      <div className="content">
        <form className="auth-form" onSubmit={handleLogin}>
          <h2 className="auth-title">Login</h2>
          <div className="form-group">
            <label className="form-label">Username:</label>
            <input
              className="form-input"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label className="form-label">Password:</label>
            <input
              className="form-input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <button className="form-button" type="submit">
            Login
          </button>
          <div className="link-container">
            <Link className="form-link" to="/register">Go to Register Page</Link>
          </div>
        </form>
        {error && <p className="error-message">{error}</p>}
      </div>
    </div>
  );
};

export default Login;