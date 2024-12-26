import '../styles/FileList.css';

const FileList = ({ files, onShare, onOpen, onDownload, onDelete, entityType }) => {
    return (
      <div className="file-list-container">
        <ul className="file-list">
          {files.length > 0 ? (
            files.map((file, index) => (
              <li key={index} className="file-list-item">
                <span className="file-name">{file}</span>
                <div className="file-buttons">

                  <button
                    className="file-open-button" 
                    onClick={() => onOpen(file)}
                  >Open</button>

                  <div>
                    {entityType != "teams" && (
                      <div>
                        <button
                        className="file-share-button" 
                        onClick={() => onShare(file)}
                      >Share</button>
                      </div>
                    )}
                  </div>
                  
                  <button 
                    className="file-download-button" 
                    onClick={() => onDownload(file)}
                  >Download</button>

                  <button 
                    className="file-delete-button" 
                    onClick={() => onDelete(file)}
                  >Delete</button>

                </div>
              </li>
            ))
          ) : (
            <p>No files found.</p>
          )}
        </ul>
      </div>
    );
  };
  
  export default FileList;
  