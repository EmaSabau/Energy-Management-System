import { deviceAPI, userAPI, monitoringAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Zap, Power, TrendingUp } from 'lucide-react';
import { useEffect, useState, useCallback } from "react";
import { useNavigate } from 'react-router-dom';
import WebSocketClient from "../services/WebSocketClient";
import './ClientDashboard.css';
import ChatWidget from "../components/ChatWidget";

export default function ClientDashboard() {
    const navigate = useNavigate();
    const [devices, setDevices] = useState([]);
    const [loading, setLoading] = useState(true);
    const { user } = useAuth();

    const fetchUserDevices = useCallback(async () => {
        if (!user || !user.username) return;
        try {
            const userData = await userAPI.getByUsername(user.username);
            const data = await deviceAPI.getUserDevices(userData.id);

            const devicesWithRealData = await Promise.all(data.map(async (device) => {
                try {
                    const today = new Date().toISOString().split('T')[0];
                    const consumptionData = await monitoringAPI.getDaily(device.id, today);

                    const currentHour = new Date().getHours();
                    const hourlyEntry = consumptionData.find(d => d.hour === currentHour);
                    const val = hourlyEntry ? hourlyEntry.value : 0;
                    return { ...device, currentConsumption: val, status: val > 0 ? 'active' : 'off' };
                } catch (e) {
                    return { ...device, currentConsumption: 0, status: 'off' };
                }
            }));

            setDevices(devicesWithRealData);
        } catch (error) {
            console.error('Error fetching devices:', error);
        } finally {
            setLoading(false);
        }
    }, [user]);

    useEffect(() => {
        if (user && user.username) {
            fetchUserDevices();
            WebSocketClient.connect(user.username, localStorage.getItem('jwt'));
            return () => WebSocketClient.disconnect();
        }
    }, [user, fetchUserDevices]);

    const totalConsumption = devices.reduce((sum, d) => sum + (d.currentConsumption || 0), 0);
    const activeCount = devices.filter(d => d.status === 'active').length;

    if (loading) return <div className="loading-screen">Se încarcă datele reale...</div>;

    return (
        <div className="dashboard-wrapper">
            <div className="dashboard-grid">

                {/* Sidebar Stânga */}
                <div className="sidebar-card">
                    <div className="house-img-container">
                        <img src={require('../assets/house.png')} alt="Casa" className="house-image" />
                    </div>
                    <h2 className="user-name">{user.username}</h2>
                    <p className="sub-text">Bun venit!</p>
                </div>

                {/* Conținut Dreapta */}
                <div className="main-content">
                    <div className="stats-grid">
                        <StatCard icon={<Power size={22} />} label="Total" val={devices.length} />
                        <StatCard icon={<Zap size={22} />} label="Consum Orar" val={`${totalConsumption.toFixed(3)} kWh`} />
                        <StatCard icon={<TrendingUp size={22} />} label="Active" val={activeCount} />
                    </div>

                    <div className="devices-section">
                        <h1 className="section-title">Dispozitive</h1>
                        <div className="devices-grid">
                            {devices.map(device => (
                                <div key={device.id} onClick={() => navigate(`/device/${device.id}`)} className="device-card">
                                    <div className="device-card-header">
                                        <h3 className="device-name">{device.name}</h3>
                                        <div className={`status-dot ${device.status}`}></div>
                                    </div>

                                    <div className="device-image-container">
                                        {device.imageUrl ? (
                                            <img
                                                src={device.imageUrl}
                                                alt={device.name}
                                                className="device-image"
                                            />
                                        ) : (
                                            <Zap size={48} color="#cbd5e1" />
                                        )}
                                    </div>

                                    <div className="consumption-value">
                                        {device.currentConsumption.toFixed(4)} kWh
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
            <ChatWidget></ChatWidget>
        </div>
    );
}

const StatCard = ({ icon, label, val }) => (
    <div className="stat-card">
        <div className="stat-icon-wrapper">{icon}</div>
        <p className="stat-label">{label}</p>
        <h3 className="stat-value">{val}</h3>
    </div>
);