import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import Header from '../components/Header';
import API from '../api';
import UserList from '../components/UserList';
import '../styles/ManageUser.css';

function ManageUser() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [users, setUsers] = useState([]);
    const [role, setRole] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    // Kullanıcıları API'den al
    const getUsers = async () => {
        try {
            const response = await API.get(`/user/list`);
            setUsers(response.data);
        } catch (err) {
            console.error("Error getting users:", err);
            setError("Failed to fetch user list. Please try again.");
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
                getUsers();
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
                        <h2 className='user-header'>User List:</h2>
                        {error ? (
                            <p className="repo-error">{error}</p>
                        ) : (
                            <UserList users={users} />
                        )}
                    </>
                ) : (
                    <p>Please log in to view users.</p>
                )}
            </div>
        </div>
    );
}

export default ManageUser;