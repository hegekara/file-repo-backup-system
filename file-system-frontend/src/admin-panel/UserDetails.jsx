import { useLocation, useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import FileList from '../components/FileList';
import { useEffect, useState } from 'react';
import API from '../api';
import "../styles/UserDetails.css";

const UserDetails = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [role, setRole] = useState("");
    const [error, setError] = useState("");
    const [files, setFiles] = useState([]);
    const { user } = location.state || {};
    const [storageLimit, setStorageLimit] = useState(user?.storageLimit || 50); // Varsayılan 50 MB

    if (!user) {
        return <p>No user details available.</p>;
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
                navigate("/");
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

    const handleUpdateStorageLimit = async (e) => {
        e.preventDefault();
        try {

            if (storageLimit <= 0) {
                setError("Storage limit must be greater than 0.");
                return;
            }

            const response = await API.patch(`/admin/update-storage-limit`, null, {
                params: {
                    userId: user.id,
                    newLimit: storageLimit,
                },
            });
            console.log(response.data);
            navigate(`/user-details/${user.id}`, { state: { user: user } });
        } catch (err) {
            console.error("Error updating storage limit:", err);
            setError(err.response?.data || "Failed to update storage limit. Please try again.");
        }
    };

    return (
        <>
            <div>
                <Header isLoggedIn={isLoggedIn} />
            </div>
            <br /><br /><br />
            <div className="user-details-container">
                {error && <p className="error-message">{error}</p>}
                {!error && (
                    <>
                        <h2 className='user-details-header'>User Details</h2>
                        <p className='user-details-info'><strong>Username:</strong> {user.username}</p>
                        <p className='user-details-info'><strong>First Name:</strong> {user.firstName}</p>
                        <p className='user-details-info'><strong>Last Name:</strong> {user.lastName}</p>
                        <form onSubmit={handleUpdateStorageLimit}>
                            <div className="form-group">
                                <label className="form-label">Storage Limit:</label>
                                <input
                                    className="form-input"
                                    type="number"
                                    value={storageLimit}
                                    onChange={(e) => setStorageLimit(e.target.value)}
                                    required
                                />
                            </div>
                            <button className="home-button" type="submit">Update Storage Limit</button>
                        </form>
                        <p className='user-details-info'><strong>Storage Limit:</strong> {user.storageLimit} MB</p>
                        <p className='user-details-info'><strong>Hashed Password:</strong> {user.password}</p>
                        <p className='user-details-info'><strong>Role:</strong> {user.role}</p>
                        <div>
                            <button className="home-button" onClick={() => handleGetRepo(user.repoPath)}>
                                Get Repo
                            </button>
                        </div>
                        {files.length > 0 && (
                            <div>
                                <FileList files={files} entityType="admin" />
                            </div>
                        )}
                    </>
                )}
            </div>
        </>
    );
};

export default UserDetails;