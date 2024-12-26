import { useNavigate } from 'react-router-dom';
import '../styles/TeamList.css';

const TeamList = ({ teams }) => {
    const navigate = useNavigate();

    const handleNavigateToRepo = (teamId) => {
        navigate(`/team-repo/${teamId}`);
    };

    return (
        <div className="team-list-container">
            <ul className="team-list">
                {teams.length > 0 ? (
                    teams.map((team, index) => (
                        <li key={index} className="team-list-item">
                            <span className="team-name">{team.name}</span>
                            <div>
                                <button onClick={() => handleNavigateToRepo(team.id)}>Get Repo</button>
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

export default TeamList;