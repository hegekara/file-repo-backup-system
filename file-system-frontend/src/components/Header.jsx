import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/Header.css";
import { FaUserCircle } from "react-icons/fa";
import { RiTeamFill } from "react-icons/ri";
import API from '../api';

const Header = ({ isLoggedIn }) => {
  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const [isTeamToggleOpen, setTeamToggleOpen] = useState(false);
  const [role, setRole] = useState(localStorage.getItem("role") || "");
  const [id, setId] = useState(localStorage.getItem("id") || "");
  const [teamName, setTeamName] = useState("");
  const [memeberName, setMemberName] = useState("");
  const navigate = useNavigate();

  const toggleDropdown = () => {
    setDropdownOpen(!isDropdownOpen);
  };

  const teamDropdown = () => {
    setTeamToggleOpen(!isTeamToggleOpen);
  };

  const handleCreateTeam = async (event) => {
    event.preventDefault();

    try {
      const team = { 
        name: teamName, 
        manager: parseInt(id, 10),
        teamMemberName: memeberName 
      };
      const response = await API.post('/team/create', team);
      console.log('Team created:', response.data);
      alert('Team created successfully');

      setDropdownOpen(false);
      setTeamName('');
      setMemberName('');
    } catch (error) {
      console.error('Error creating team:', error);
      alert('Failed to create team');
    }
  };

  const logOut = () => {
    localStorage.clear();
    navigate("/home");
    window.location.reload();
  };

  return (
    <header className="header">
      {role==="role_user" && (
      <Link to="/" className="header-logo">
        Backup & Share
      </Link>
      )}

      {((role==="role_admin")) && (
      <Link to="/admin-panel" className="header-logo">
        Backup & Share
      </Link>
      )}

      {isLoggedIn && (

        <div>
          <div className="header-right">
            <div className="create-team">
            <RiTeamFill
                  className="team-icon"
                  onClick={teamDropdown}
                  size={32}
                />
                {isTeamToggleOpen && (
                  <div>
                    <div className="team-dropdown-menu">
                      <h3 className="team-title">Create Team</h3>
                      <form onSubmit={handleCreateTeam}>
                        <input
                          className="team-dropdown-item"
                          type="text"
                          placeholder="Team Name"
                          value={teamName}
                          onChange={(e) => setTeamName(e.target.value)}
                          required
                        />
                        <input
                          className="team-dropdown-item"
                          type="text"
                          placeholder="Username"
                          value={memeberName}
                          onChange={(e) => setMemberName(e.target.value)}
                          required
                        />
                        <button type="submit" className="create-team-button">Create Team</button>
                      </form>
                    </div>
                  </div>
                )}
            </div>
            
          
            <div className="user-menu">
              <FaUserCircle
                className="user-icon"
                onClick={toggleDropdown}
                size={32}
              />
              {isDropdownOpen && (
                <div className="dropdown-menu">
                  <Link to="/profile" className="dropdown-item">Profil</Link>
                  <Link to="/password-settings" className="dropdown-item">Change Password</Link>
                  {(role === "role_user") && (
                    <Link to="/list-booking" className="dropdown-item">My Teams</Link>
                  )}
                  <button onClick={logOut} className="dropdown-item">Log Out</button>
                </div>
              )}
            </div>
          </div>

        </div>
        
      )}
    </header>
  );
};

export default Header;