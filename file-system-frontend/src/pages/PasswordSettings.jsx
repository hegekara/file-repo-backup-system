import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import API from "../api";
import Header from "../components/Header";
import "../styles/PasswordSettings.css";

function PasswordSettings() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [newPassword2, setNewPassword2] = useState("");
  const [id] = useState(localStorage.getItem("id"));
  const [role, setRole] = useState(localStorage.getItem("role"));
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    const role = localStorage.getItem("role");
    setIsLoggedIn(!!token);
    setRole(role);

    if (!token) {
      navigate("/");
    }
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!currentPassword || !newPassword || !newPassword2) {
      setMessage("Lütfen tüm alanları doldurun.");
      return;
    }

    if (newPassword !== newPassword2) {
      setMessage("Yeni şifreler eşleşmiyor!");
      return;
    }

    if(role === "role_user"){
      try {
        const response = await API.patch(`/user/change-password/${id}`, null, {
          params: {
            oldPassword: currentPassword,
            newPassword: newPassword,
          },
        });
  
        if (response.status === 200) {
          setMessage("Şifre başarıyla değiştirildi!");
          setCurrentPassword("");
          setNewPassword("");
          setNewPassword2("");
          navigate("/");
        } else {
          setMessage("Şifre değiştirme başarısız oldu. Tekrar deneyin.");
        }
      } catch (error) {
        console.error("Şifre değiştirme hatası:", error);
        if (error.response && error.response.status === 401) {
          setMessage("Mevcut şifre hatalı.");
        } else {
          setMessage("Bir hata oluştu. Lütfen daha sonra tekrar deneyin.");
        }
      }
    }
  };

  return (
    <div>
      {isLoggedIn ? (
        <>
          <Header isLoggedIn={isLoggedIn} />
          <br />
          <div className="password-settings-form-container">
            <h1>Şifre Ayarları</h1>
            <form onSubmit={handleSubmit} className="password-settings-form">
              <div className="form-group">
                <label className="form-label">Mevcut Şifre:</label>
                <input
                  className="form-input"
                  type="password"
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label className="form-label">Yeni Şifre:</label>
                <input
                  className="form-input"
                  type="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label className="form-label">Yeni Şifre (Tekrar):</label>
                <input
                  className="form-input"
                  type="password"
                  value={newPassword2}
                  onChange={(e) => setNewPassword2(e.target.value)}
                  required
                />
              </div>
              <button type="submit" className="form-button">
                Şifreyi Değiştir
              </button>
            </form>
            {message && <p className="form-message">{message}</p>}
          </div>
        </>
      ) : (
        <p>Lütfen giriş yapın.</p>
      )}
    </div>
  );
}

export default PasswordSettings;