import '../styles/FileList.css';

const FileList = ({ files, onShare, onOpen, onDownload, onDelete, entityType }) => {
    return (
        <div className="file-list-container">
            <ul className="file-list">
                {files.length > 0 ? (
                    files.map((file) => (
                        <li key={file} className="file-list-item">
                            <span className="file-name">{file}</span>
                            {entityType !== "admin" && (
                                <div className="file-buttons">
                                    <button
                                        className="file-open-button"
                                        onClick={() => onOpen(file)}
                                    >
                                        Open
                                    </button>
                                    {entityType !== "teams" && (
                                        <button
                                            className="file-share-button"
                                            onClick={() => onShare(file)}
                                        >
                                            Share
                                        </button>
                                    )}
                                    <button
                                        className="file-download-button"
                                        onClick={() => onDownload(file)}
                                    >
                                        Download
                                    </button>
                                    <button
                                        className="file-delete-button"
                                        onClick={() => onDelete(file)}
                                    >
                                        Delete
                                    </button>
                                </div>
                            )}
                        </li>
                    ))
                ) : (
                    <p className="no-files-message">No files available to display.</p>
                )}
            </ul>
        </div>
    );
};

export default FileList;