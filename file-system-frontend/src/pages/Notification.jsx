import React, { useState, useEffect } from "react";
import API from "../api";
import Header from "../components/Header";
import NotificationList from "../components/NotificationList";
import "../styles/Repo.css";

function Notification() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [role, setRole] = useState(localStorage.getItem("role") || "");
  const [id, setId] = useState(localStorage.getItem("id") || "");
  const [notificationList, setNotificationList] = useState([]);
  const [error, setError] = useState("");

  const getNotifications = async () => {
    if (!id || !role) {
      setError("Invalid user credentials.");
      return;
    }

    try {
      const endpoint =
        role === "role_user"
          ? `/notification/get/${id}`
          : `/admin-notification/get/${id}`;

      const response = await API.get(endpoint);
      setNotificationList(response.data);
      setError(""); // Hatalar başarılı işlem sonrası temizlenir
    } catch (err) {
      console.error("Error fetching notifications:", err);
      setError("Failed to fetch notifications. Please try again.");
    }
  };

  const handleDeleteNotification = async (notificationId) => {
    try {
      const endpoint =
        role === "role_user"
          ? `/notification/${notificationId}`
          : `/admin-notification/${notificationId}`;

      await API.delete(endpoint);

      // Bildirimi başarılı şekilde silindikten sonra listeden kaldır
      setNotificationList((prevList) =>
        prevList.filter((notification) => notification.id !== notificationId)
      );
      setError(""); // Hatalar başarılı işlem sonrası temizlenir
    } catch (err) {
      console.error("Error deleting notification:", err);
      setError("Failed to delete notification. Please try again.");
    }
  };

  useEffect(() => {
    if (localStorage.getItem("jwtToken")) {
      setIsLoggedIn(true);
      getNotifications();
    }
  }, []);

  return (
    <div>
      <Header isLoggedIn={isLoggedIn} />
      <br />
      <div className="repo-container">
        <h1 className="repo-header">Notifications</h1>
        {isLoggedIn ? (
          <>
            {error && <p className="repo-error">{error}</p>}
            <NotificationList
              notifications={notificationList}
              onDelete={handleDeleteNotification}
            />
          </>
        ) : (
          <p>Please log in to view notifications.</p>
        )}
      </div>
    </div>
  );
}

export default Notification;