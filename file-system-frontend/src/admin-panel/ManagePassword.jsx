import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import Header from '../components/Header';
import API from '../api';
import '../styles/ManageUser.css';
import RequestList from '../components/PasswordRequestList';

function ManagePassword() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [requests, setRequests] = useState([]);
    const [role, setRole] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const getRequests = async () => {
        try {
            const response = await API.get(`/admin/get-password-requests`);
            setRequests(response.data);
        } catch (err) {
            console.error("Error getting requests:", err);
            setError("Failed to fetch request list. Please try again.");
        }
    };

    useEffect(() => {
        const token = localStorage.getItem("jwtToken");
        const userRole = localStorage.getItem("role") || "";

        if (!token) {
            navigate("/");
        } else {
            setIsLoggedIn(true);
            setRole(userRole.toLowerCase());

            if (userRole.toLowerCase() === "role_admin") {
                getRequests();
            } else {
                setError("You are not authorized to view this page.");
            }
        }
    }, [navigate]);

    return (
        <div>
            <Header isLoggedIn={isLoggedIn} /><br />
            <div className='user-container'>
                {isLoggedIn ? (
                    <>
                        <h2 className='user-header'>Request List:</h2>
                        {error ? (
                            <p className="repo-error">{error}</p>
                        ) : requests.length > 0 ? (
                            <RequestList requests={requests} />
                        ) : (
                            <p>No password reset requests found.</p>
                        )}
                    </>
                ) : (
                    <p>Please log in to view users.</p>
                )}
            </div>
        </div>
    );
}

export default ManagePassword;