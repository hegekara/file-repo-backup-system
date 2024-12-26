import React, { useState, useEffect } from 'react';
import API from '../api';
import Header from '../components/Header';
import FileList from '../components/FileList';
import '../styles/Repo.css';

function Repo() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [entityType, setEntityType] = useState("users");
  const [id, setId] = useState(localStorage.getItem("id") || "");
  const [fileList, setFileList] = useState([]);
  const [error, setError] = useState("");
  const [teams, setTeams] = useState([]);

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
      console.error("Error uploading file:", err);
      setError("Failed to upload file. Please try again.");
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

  const handleShare = async(fileName) => {
    try {
        const response = await API.post(`/files/share/${entityType}/${id}/${fileName}/to/${teamId}`);
        console.log(response);
        
    } catch (error) {
        console.log(error);
        setError("File could not be shared");
    }
  }

  const handleGetUserTeams = async(e) => {
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
                onShare={handleShare}/>
          </>
        ) : (
          <p>Please log in to view files.</p>
        )}
      </div>
    </div>
  );
}

export default Repo;
