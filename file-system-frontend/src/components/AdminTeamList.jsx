import { useNavigate } from 'react-router-dom';
import '../styles/userList.css';
import { useState } from 'react';
import API from '../api';

const AdminTeamList = ({ teams }) => {
    const navigate = useNavigate();
    const [error, setError] = useState("");

    const handleViewDetails = async (teamId) => {
        try {
            const response = await API.get(`/team/${teamId}`);
            console.log("Team Details:", response.data);
            navigate(`/team-details/${teamId}`, { state: { team: response.data } });
        } catch (err) {
            console.error("Error fetching team details:", err);
            setError("Failed to fetch team details. Please try again.");
        }
    };

    return (
        <div className="user-list-container">
            {error && <p className="error-message">{error}</p>}
            <ul className="user-list">
                {teams.length > 0 ? (
                    teams.map((team) => (
                        <li key={team.id} className="user-list-item">
                            <span className="user-name">{team.name}</span>
                            <div className="action-buttons">
                                <button className='action-button' onClick={() => handleViewDetails(team.id)}>View Details</button>
                            </div>
                        </li>
                    ))
                ) : (
                    <p>No teams found.</p>
                )}
            </ul>
        </div>
    );
};

export default AdminTeamList;