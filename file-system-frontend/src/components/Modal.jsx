import React from 'react';
import '../styles/Modal.css';

const Modal = ({ title, children, onClose }) => {
  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <button className="modal-close-button" onClick={onClose}>
          &times;
        </button>
        <h2>{title}</h2>
        <div className="modal-body">{children}</div>
      </div>
    </div>
  );
};

export default Modal;