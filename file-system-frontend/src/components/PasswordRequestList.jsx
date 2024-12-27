import { useNavigate } from 'react-router-dom';
import '../styles/UserList.css';
import { useState } from 'react';
import API from '../api';

const RequestList = ({ requests }) => {
    const navigate = useNavigate();
    const [error, setError] = useState("");

    const handleApprove = async (requestId) => {
        try {
            const response = await API.post(`/admin/password/approve/${requestId}`);
            window.location.reload();
        } catch (err) {
            console.error("Error fetching user details:", err);
            setError("Failed to fetch user details. Please try again.");
        }
    };

    const handleReject = async (requestId) => {
        try {
            const response = await API.post(`/admin/password/reject/${requestId}`);
            window.location.reload();
        } catch (err) {
            console.error("Error fetching user details:", err);
            setError("Failed to fetch user details. Please try again.");
        }
    };

    return (
        <div className="user-list-container">
            {error && <p className="error-message">{error}</p>}
            <ul className="user-list">
                {requests.length > 0 ? (
                    requests.map((request) => (
                        <li key={request.id} className="user-list-item">
                            <span className="user-name">{request.user.username}</span>
                            <div className="action-buttons">

                                <button style={{'backgroundColor': '#00ac00'}} 
                                className="action-button"
                                onClick={() => handleApprove(request.id)}>Approve</button>

                                <button style={{'backgroundColor': '#d00000'}} 
                                className="action-button"
                                onClick={() => handleReject(request.id)}>Reject</button>

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

export default RequestList;