import React from "react";
import { FaCheck } from "react-icons/fa";
import "../styles/NotificationList.css"; // Stil dosyasını eklemeyi unutmayın

function NotificationList({ notifications, onDelete }) {
  if (!notifications.length) {
    return <p>No notifications available.</p>;
  }

  return (
    <ul className="notification-list">
      {notifications.map((notification) => (
        <li key={notification.id} className="notification-item">
          <p>{notification.message}</p>
          <FaCheck
            className="delete-icon"
            size={20}
            onClick={() => onDelete(notification.id)}
          />
        </li>
      ))}
    </ul>
  );
}

export default NotificationList;