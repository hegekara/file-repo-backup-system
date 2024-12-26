import React, {useState, useEffect} from 'react'
import Header from '../components/Header';
import TeamList from '../components/TeamList';
import API from '../api';
import '../styles/MyTeams.css'

function MyTeams() {

    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [entityType, setEntityType] = useState("teams");
    const [id, setId] = useState(localStorage.getItem("id") || '');
    const [error, setError] = useState("");
    const [teams, setTeams] = useState([]);

    const handleGetUserTeams = async (e) => {
        try {
            const response = await API.get(`/team/user/${id}`);
            setTeams(response.data);

        } catch (error) {
            console.log(error);
            setError("File could not be shared");
        }
    }

    useEffect(() => {
        if (localStorage.getItem("jwtToken")) {
            setIsLoggedIn(true);
            handleGetUserTeams();
        }
    }, []);


    return (
        <div>
            <div>
                <Header isLoggedIn={isLoggedIn}/>
            </div><br />
            <div className='team-container'>
            
                {isLoggedIn ? (
                <>
                    <h2 className='team-header'>Team List:</h2>
                    {error && <p className="repo-error">{error}</p>}
                    <TeamList teams={teams}/>
                </>
                ) : (
                <p>Please log in to view teams.</p>
                )}
            </div>
        </div>
    )
}

export default MyTeams