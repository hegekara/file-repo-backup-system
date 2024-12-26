import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api';
import Header from '../components/Header';
import "../styles/Profile.css";

function Profile() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [id, setId] = useState(localStorage.getItem("id") || "");
    const [username, setUsername] = useState("");
    const [role] = useState(localStorage.getItem("role") || "");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("jwtToken");
        setIsLoggedIn(!!token);
    
        if (!token || !id) {
            alert("You are not authorized. Redirecting to login.");
            navigate("/");
            return;
        }
    
        API.get(`/user/${id}`)
            .then((response) => {
                const user = response.data;
                setUsername(user.username);
                setFirstName(user.firstName);
                setLastName(user.lastName);
            })
            .catch((error) => {
                console.error("Error fetching user data:", error);
                alert("Failed to fetch user data.");
            });
    }, [id, navigate]);

    const handleUpdate = async (e) => {
        e.preventDefault();
    
        try {
            const response = await API.put(`/user/${id}`, {
                username,
                firstName,
                lastName,
            });
    
            alert("Profile updated successfully!");
            console.log("Updated user:", response.data);
        } catch (error) {
            console.error("Error updating profile:", error);
            alert("Failed to update profile.");
        }
    };

    return (
        <div>
            {isLoggedIn && (
                <>
                    <Header isLoggedIn={isLoggedIn} />
                    <br />
                    <div className="profile-container">
                        <h2>Profile</h2>
                        <form onSubmit={handleUpdate}>
                            <div className="form-group">
                                <label>Username:</label>
                                <input
                                    type="text"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                />
                            </div>
                            <div className="form-group">
                                <label>First Name:</label>
                                <input
                                    type="text"
                                    value={firstName}
                                    onChange={(e) => setFirstName(e.target.value)}
                                />
                            </div>
                            <div className="form-group">
                                <label>Last Name:</label>
                                <input
                                    type="text"
                                    value={lastName}
                                    onChange={(e) => setLastName(e.target.value)}
                                />
                            </div>
                            <button className="update-profile-button" type="submit">
                                Update Profile
                            </button>
                        </form>
                    </div>
                </>
            )}
        </div>
    );
}

export default Profile;