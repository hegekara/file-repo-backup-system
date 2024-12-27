import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import Header from '../components/Header';
import API from '../api';
import UserList from '../components/UserList';
import '../styles/ManageUser.css';
import TeamList from '../components/TeamList';
import AdminTeamList from '../components/AdminTeamList';

function ManageTeam() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [teams, setTeams] = useState([]);
    const [role, setRole] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    
    const getTeams = async () => {
        try {
            const response = await API.get(`/team/list`);
            console.log(response);
            setTeams(response.data);
        } catch (err) {
            console.error("Error getting teams:", err);
            setError("Failed to fetch team list. Please try again.");
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
                getTeams();
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
                        <h2 className='user-header'>Team List:</h2>
                        {error ? (
                            <p className="repo-error">{error}</p>
                        ) : (
                            <AdminTeamList teams={teams} />
                        )}
                    </>
                ) : (
                    <p>Please log in to view teamss.</p>
                )}
            </div>
        </div>
    );
}

export default ManageTeam;