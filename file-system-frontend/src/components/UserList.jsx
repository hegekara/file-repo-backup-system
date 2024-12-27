import { useNavigate } from 'react-router-dom';
import '../styles/userList.css';
import { useState } from 'react';
import API from '../api';

const UserList = ({ users }) => {
    const navigate = useNavigate();
    const [error, setError] = useState("");

    const handleViewDetails = async (userId) => {
        try {
            const response = await API.get(`/user/${userId}`);
            console.log("User Details:", response.data);
            navigate(`/user-details/${userId}`, { state: { user: response.data } });
        } catch (err) {
            console.error("Error fetching user details:", err);
            setError("Failed to fetch user details. Please try again.");
        }
    };

    return (
        <div className="user-list-container">
            {error && <p className="error-message">{error}</p>}
            <ul className="user-list">
                {users.length > 0 ? (
                    users.map((user) => (
                        <li key={user.id} className="user-list-item">
                            <span className="user-name">{user.username}</span>
                            <div className="action-buttons">
                                <button className='action-button' onClick={() => handleViewDetails(user.id)}>View Details</button>
                            </div>
                        </li>
                    ))
                ) : (
                    <p>No users found.</p>
                )}
            </ul>
        </div>
    );
};

export default UserList;