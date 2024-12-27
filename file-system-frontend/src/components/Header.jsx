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
  const [error, setError] = useState("");
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


  const RequestPasswordChange= async (event) => {
    event.preventDefault();

    try {
      const response = await API.post(`/user/${id}/request-password-change`);
      console.log('Password change request sent:', response.data);
      alert('Password change request sent');

    } catch (error) {
      console.error('Error:', error);
      alert('Failed to sent request');
    }
  };


  const handleDownloadLogs = async (event) => {
    event.preventDefault();

    try {
        const response = await API.get(`/files/download-logs`, {
            responseType: 'blob', // Ensure the response is treated as binary data (Blob)
        });

        const fileName = "logs.zip"; // Set the name of the downloaded file
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
        link.remove(); // Cleanup
    } catch (err) {
        console.error("Error downloading file:", err);
        alert("Failed to download logs. Please try again."); // User-friendly feedback
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
            {(role === "role_user") &&(
              <div className="create-team">
              <RiTeamFill
                    className="team-icon"
                    onClick={teamDropdown}
                    size={32}
                  />
                  {isTeamToggleOpen &&(
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
            )}


            <div className="user-menu">
              <FaUserCircle
                className="user-icon"
                onClick={toggleDropdown}
                size={32}
              />
              {isDropdownOpen && (
                <div className="dropdown-menu">

                  {(role === "role_user") && (
                    <div>
                      <Link to="/profile" className="dropdown-item">Profil</Link>
                      <button onClick={RequestPasswordChange} className="dropdown-item">Request Change Password</button>
                      <Link to="/my-teams" className="dropdown-item">My Teams</Link>
                    </div>

                  )}

                  {(role === "role_admin") && (
                    <div>
                      <button onClick={handleDownloadLogs} className="dropdown-item">Download Logs</button>
                    </div>
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