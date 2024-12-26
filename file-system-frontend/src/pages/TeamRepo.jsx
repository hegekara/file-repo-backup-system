import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import API from '../api';
import Header from '../components/Header';
import FileList from '../components/FileList';

function TeamRepo() {
    const { teamId } = useParams(); // URL'den teamId alınıyor
    const [fileList, setFileList] = useState([]);
    const [error, setError] = useState("");
    const entityType = "teams";
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const id = teamId

    const handleAPIError = (message) => {
        console.error(message);
        setError(message);
    };

    const handleGetRepo = async () => {
        try {
            const response = await API.get('/files/list', {
                params: { entityType:"teams", id: teamId },
            });
            setFileList(response.data);
        } catch {
            handleAPIError("Failed to fetch file list. Please try again.");
        }
    };

    const handleDownload = async (fileName) => {
        try {
            const response = await API.get(`/files/download`, {
                params: { entityType:"teams", id: teamId, fileName },
                responseType: 'blob',
            });

            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (err) {
            console.error("Error downloading file:", err);
            setError("Failed to download file. Please try again.");
        }
    }


    const handleDelete = async (fileName) => {
        try {
            await API.delete(`/files/delete/${entityType}/${id}/${fileName}`);
            handleGetRepo();

        } catch (error) {
            console.log(error);
            setError("Failed to delete")
        }
    }

    const handleOpen = async (fileName) => {
        try {
            const response = await API.get(`/files/open/${entityType}/${id}/${fileName}`);
            console.log(response);

        } catch (error) {
            console.log(error);
            setError("File could not be opened");
        }
    }

    useEffect(() => {
        if (localStorage.getItem('jwtToken')) {
            setIsLoggedIn(true);
            handleGetRepo();
        }
    }, []);

    return (
        <div>
            <Header isLoggedIn={isLoggedIn} /><br />
            <div className="repo-container">
                <h1 className="repo-header">Team Repo</h1>
                {error && <p className="repo-error">{error}</p>}
                <FileList files={fileList}
                    entityType={"teams"}
                    onOpen={handleOpen}
                    onDownload={handleDownload}
                    onDelete={handleDelete} />
            </div>
        </div>
    );
}

export default TeamRepo;