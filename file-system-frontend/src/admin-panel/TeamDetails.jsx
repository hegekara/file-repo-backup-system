import { useLocation, useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import FileList from '../components/FileList';
import { useEffect, useState } from 'react';
import API from '../api';
import "../styles/UserDetails.css"; 

const TeamDetails = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [role, setRole] = useState("");
    const [error, setError] = useState("");
    const [files, setFiles] = useState([]);
    const { team } = location.state || {};

    if (!team) {
        return <p>No team details available.</p>;
    }

    useEffect(() => {
        const token = localStorage.getItem("jwtToken");
        const userRole = localStorage.getItem("role") || "";

        if (!token) {
            navigate("/");
        } else {
            setIsLoggedIn(true);
            setRole(userRole.toLowerCase());

            if (userRole.toLowerCase() !== "role_admin") {
                setError("You are not authorized to view this page.");
            }
        }
    }, [navigate]);

    const handleGetRepo = async (path) => {
        try {
            const response = await API.get(`/files/getRepo`, { params: { path } });
            console.log("Repo Data:", response.data);
            setFiles(response.data);
        } catch (err) {
            console.error("Error getting repo:", err);
            setError("Failed to fetch repo. Please try again.");
        }
    };

    return (
        <>
            <div>
                <Header isLoggedIn={isLoggedIn} />  
            </div><br /><br /><br />
            <div className="user-details-container">
                {error && <p className="error-message">{error}</p>}
                {!error && (
                    <>
                        <h2 className='user-details-header'>Team Details</h2>
                        <p className='user-details-info'><strong>Team name:</strong> {team.name}</p>
                        <p className='user-details-info'><strong>Team manager:</strong> {team.manager.firstName} {team.manager.lastName}</p>
                        <p className='user-details-info'><strong>Team member:</strong> {team.member.firstName} {team.member.lastName}</p>
                        <div>
                            <button className="home-button" onClick={() => handleGetRepo(team.repoPath)}>
                                Get Repo
                            </button>
                        </div>
                        {files.length > 0 && (
                            <div>
                                <FileList files={files} entityType="admin"/>
                            </div>
                        )}
                    </>
                )}
            </div>
        </>
    );
};

export default TeamDetails;