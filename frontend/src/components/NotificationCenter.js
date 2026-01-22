import React, { useEffect, useState } from 'react';
import ReactDOM from 'react-dom';
import { useAuth } from '../context/AuthContext';
import ws from '../services/WebSocketClient';
import './NotificationCenter.css';

const NotificationCenter = ({ userId }) => {
    const { user, isAdmin } = useAuth();
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        if (user && !isAdmin()) {
            const unsubscribe = ws.onNotification((notif) => {
                console.log(" Notificare primită:", notif);
                setNotifications(prev => [notif, ...prev]);
                setTimeout(() => {
                    setNotifications(prev => prev.filter(n => n !== notif));
                }, 10000);
            });

            ws.connect(user.username, localStorage.getItem('jwt'));

            return () => unsubscribe();
        }
    }, [user, isAdmin]);

    if (!user || isAdmin() || notifications.length === 0) return null;
    return ReactDOM.createPortal(
        <div className="notifications-container">
            {notifications.map((n, i) => (
                <div key={i} className={`notification-toast ${n.type || 'overconsumption'}`}>
                    <div className="toast-content">
                        <div className="toast-title">{n.title || 'Alertă Consum'}</div>
                        <div className="toast-message">{n.message}</div>
                        <small className="notif-time">
                            {new Date(n.timestamp || Date.now()).toLocaleTimeString()}
                        </small>
                    </div>
                    <button
                        className="toast-close"
                        onClick={() => setNotifications(prev => prev.filter((_, idx) => idx !== i))}
                    >
                        &times;
                    </button>
                </div>
            ))}
        </div>,
        document.body
    );
};

export default NotificationCenter;