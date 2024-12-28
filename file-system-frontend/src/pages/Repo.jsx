import React, { useState, useEffect } from 'react';
import API from '../api';
import Header from '../components/Header';
import Modal from '../components/Modal';
import FileList from '../components/FileList';
import '../styles/Repo.css';

function Repo() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [entityType, setEntityType] = useState("users");
  const [id, setId] = useState(localStorage.getItem("id") || "");
  const [fileList, setFileList] = useState([]);
  const [error, setError] = useState("");
  const [teams, setTeams] = useState([]);
  const [selectedFile, setSelectedFile] = useState(""); // Paylaşılacak dosya
  const [isModalOpen, setIsModalOpen] = useState(false); // Popup kontrolü

  const handleGetRepo = async () => {
    if (!entityType || !id) {
      setError("Entity type or ID is missing.");
      return;
    }

    try {
      const response = await API.get(`/files/list`, {
        params: { entityType, id },
      });

      console.log("Response:", response.data);
      setFileList(response.data);
    } catch (err) {
      console.error("Error fetching files:", err);
      setError("Failed to fetch file list. Please try again.");
    }
  };

  const handleUploadFile = async (e) => {
    e.preventDefault();

    const fileInput = e.target.elements.file;
    if (!entityType || !id || !fileInput.files[0]) {
      setError("Please select a file and provide the required information.");
      return;
    }

    const formData = new FormData();
    formData.append("file", e.target.file.files[0]);

    try {
      const response = await API.post(`/files/upload/${entityType}/${id}`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      console.log("File upload response:", response.data);
      setError("");
      handleGetRepo();
    } catch (err) {
      if (err.response && err.response.status === 509){
        setError(err.response.data || "Storage limit exceeded. File not uploaded.");
      } else {
        console.error("Error uploading file:", err);
        setError("Failed to upload file. Please try again.");
      }
    }
  };


  const handleDownload = async(fileName) => {
    try {
        const response = await API.get(`/files/download`, {
            params: {entityType, id, fileName},
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


  const handleDelete = async(fileName) => {
    try {
        await API.delete(`/files/delete/${entityType}/${id}/${fileName}`);
        handleGetRepo();
        
    } catch (error) {
        console.log(error);
        setError("Failed to delete")
    }
  }

  const handleOpen = async(fileName) => {
    try {
        const response = await API.get(`/files/open/${entityType}/${id}/${fileName}`);
        console.log(response);
        
    } catch (error) {
        console.log(error);
        setError("File could not be opened");
    }
  }

  const handleShare = async (fileName, teamId) => {
    try {
      const response = await API.post(`/files/share/${entityType}/${id}/${fileName}/to/${teamId}`);
      console.log("File shared:", response.data);
      setError("");
      setIsModalOpen(false); // Popup'u kapat
    } catch (error) {
      setError("File could not be shared");
    }
  };

  const handleGetUserTeams = async () => {
    try {
      const response = await API.get(`/team/user/${id}`);
      setTeams(response.data);
    } catch (error) {
      setError("User team could not be found");
    }
  };

  const openShareModal = (fileName) => {
    setSelectedFile(fileName);
    setIsModalOpen(true);
  };

  const closeShareModal = () => {
    setSelectedFile("");
    setIsModalOpen(false);
  };



  useEffect(() => {
    if (localStorage.getItem("jwtToken")) {
      setIsLoggedIn(true);
      handleGetRepo();
      handleGetUserTeams();
    }
  }, []);

  return (
    <div>
      <div>
        <Header isLoggedIn={isLoggedIn} />
      </div>
      <br />
      <div className="repo-container">
        <div>
          <h1 className="repo-header">Repo</h1>
          <form onSubmit={handleUploadFile} className="repo-form">
            <input type="file" name="file" accept="*/*" />
            <button type="submit">Upload File</button>
          </form>
        </div>
        {isLoggedIn ? (
          <>
            <h2>File List:</h2>
            {error && <p className="repo-error">{error}</p>}
            <FileList files={fileList} 
                onDownload={handleDownload}
                onDelete={handleDelete}
                onOpen={handleOpen}
                onShare={openShareModal}/>
                {isModalOpen && (
                  <Modal 
                    title="Share File" 
                    onClose={closeShareModal}
                  >
                    <h3>Share "{selectedFile}" with:</h3>
                    <ul className="modal-team-list">
                      {teams.map((team) => (
                        <li className="modal-team-item" key={team.id}>
                          <span className="modal-team-name">{team.name}</span>
                          <button
                            className="modal-share-button"
                            onClick={() => handleShare(selectedFile, team.id)}
                          >
                            Share
                          </button>
                        </li>
                      ))}
                    </ul>
                  </Modal>
                )}
          </>
        ) : (
          <p>Please log in to view files.</p>
        )}
      </div>
    </div>
  );
}

export default Repo;
