import React, { useState, useEffect } from 'react';
import { userAPI, deviceAPI } from '../services/api';
import { Link2, Unlink, Users } from 'lucide-react';

export default function AssignDevices() {
    const [users, setUsers] = useState([]);
    const [devices, setDevices] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const [userDevices, setUserDevices] = useState([]);
    const [availableDevices, setAvailableDevices] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => { fetchData(); }, []);
    useEffect(() => { if (selectedUser) fetchUserDevices(selectedUser.id); }, [selectedUser]);

    const fetchData = async () => {
        try {
            const [usersData, devicesData] = await Promise.all([
                userAPI.getAll(),
                deviceAPI.getAll()
            ]);

            const clients = usersData.filter(u => {
                const role = (u.role || '').toUpperCase();
                return role !== 'ROLE_ADMIN' && role !== 'ADMIN';
            });

            setUsers(clients);
            setDevices(devicesData);
        } catch {
            alert('Eroare la încărcarea datelor');
        } finally {
            setLoading(false);
        }
    };

    const fetchUserDevices = async (userId) => {
        const userDevs = await deviceAPI.getUserDevices(userId);
        setUserDevices(userDevs);
        const userDevIds = userDevs.map(d => d.id);
        setAvailableDevices(devices.filter(d => !userDevIds.includes(d.id)));
    };

    const handleAssign = async (deviceId) => {
        await deviceAPI.assignToUser(deviceId, selectedUser.id);
        fetchUserDevices(selectedUser.id);
    };

    const handleUnassign = async (deviceId) => {
        if (!window.confirm('Sigur elimini acest dispozitiv?')) return;
        await deviceAPI.unassignFromUser(deviceId);
        fetchUserDevices(selectedUser.id);
    };

    if (loading) return <div style={{ padding: 40, textAlign: 'center' }}>Se încarcă...</div>;

    return (
        <div style={{
            padding: '40px',
            maxWidth: '1400px',
            margin: '0 auto'
        }}>
            <div style={{ marginBottom: '30px' }}>
                <h1 style={{ fontSize: '32px', fontWeight: '700', color: '#0f3b23' }}>
                    Asignare Dispozitive
                </h1>
                <p style={{ color: '#4b7358' }}>
                    Gestionează dispozitivele asociate fiecărui utilizator
                </p>
            </div>

            <div style={{
                background: '#d8f5df',
                borderRadius: '20px',
                padding: '30px',
                boxShadow: '0 4px 10px rgba(0,0,0,0.06)'
            }}>

                <div style={{
                    display: 'grid',
                    gridTemplateColumns: '300px 1fr',
                    gap: '24px'
                }}>

                    {/* LISTA UTILIZATORI */}
                    <div style={{
                        background: 'white',
                        borderRadius: '12px',
                        padding: '20px',
                        border: '2px solid #b6e6c7'
                    }}>
                        <h3 style={{
                            fontSize: '18px',
                            fontWeight: '600',
                            color: '#0f3b23',
                            marginBottom: '16px',
                            display: 'flex',
                            alignItems: 'center',
                            gap: 8
                        }}>
                            <Users size={20} color="#0f3b23" />
                            Utilizatori
                        </h3>

                        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
                            {users.map(user => (
                                <button
                                    key={user.id}
                                    onClick={() => setSelectedUser(user)}
                                    style={{
                                        padding: '10px 16px',
                                        background: selectedUser?.id === user.id ? '#b6e6c7' : '#f3faf5',
                                        border: '2px solid',
                                        borderColor: selectedUser?.id === user.id ? '#0a6035' : 'transparent',
                                        borderRadius: '10px',
                                        cursor: 'pointer',
                                        fontSize: '14px',
                                        fontWeight: '600',
                                        color: selectedUser?.id === user.id ? '#0a6035' : '#1e4630'
                                    }}
                                >
                                    {user.username}
                                </button>
                            ))}
                        </div>
                    </div>

                    {/* MANAGERIERE DISPOZITIVE */}
                    <div>
                        {!selectedUser ? (
                            <div style={{
                                background: 'white',
                                borderRadius: '12px',
                                padding: '60px',
                                textAlign: 'center',
                                border: '2px solid #b6e6c7'
                            }}>
                                <Users size={48} color="#5b8970" />
                                <p style={{ color: '#4b7358', marginTop: 12 }}>
                                    Selectează un utilizator
                                </p>
                            </div>
                        ) : (
                            <div>

                                {/* DISPOZITIVE ASIGNATE */}
                                <div style={{
                                    background: 'white',
                                    padding: '24px',
                                    borderRadius: '12px',
                                    border: '2px solid #b6e6c7',
                                    marginBottom: 24
                                }}>
                                    <h3 style={{ fontSize: 18, fontWeight: 600, color: '#0f3b23', marginBottom: 12 }}>
                                        Dispozitive asignate lui {selectedUser.username}
                                    </h3>

                                    {userDevices.length === 0 ? (
                                        <p style={{ color: '#4b7358', textAlign: 'center', padding: 20 }}>
                                            Niciun dispozitiv asignat
                                        </p>
                                    ) : (
                                        <div style={{
                                            display: 'grid',
                                            gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
                                            gap: 16
                                        }}>
                                            {userDevices.map(device => (
                                                <div key={device.id} style={{
                                                    background: '#f1fcf4',
                                                    border: '1px solid #c2edd1',
                                                    borderRadius: 10,
                                                    padding: 14
                                                }}>
                                                    <strong style={{ color: '#0f3b23' }}>{device.name}</strong>
                                                    <p style={{ fontSize: 13, color: '#4b7358', margin: '6px 0' }}>
                                                        {device.description || 'Fără descriere'}
                                                    </p>
                                                    <button
                                                        onClick={() => handleUnassign(device.id)}
                                                        style={{
                                                            width: '100%',
                                                            padding: 8,
                                                            border: 'none',
                                                            borderRadius: 6,
                                                            background: '#c6f0cc',
                                                            color: '#0a6035',
                                                            cursor: 'pointer',
                                                            fontWeight: 600,
                                                            display: 'flex',
                                                            gap: 6,
                                                            justifyContent: 'center'
                                                        }}
                                                    >
                                                        <Unlink size={14} />
                                                        Elimină
                                                    </button>
                                                </div>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                {/* DISPOZITIVE DISPONIBILE */}
                                <div style={{
                                    background: 'white',
                                    padding: '24px',
                                    borderRadius: '12px',
                                    border: '2px solid #b6e6c7'
                                }}>
                                    <h3 style={{ fontSize: 18, fontWeight: 600, color: '#0f3b23', marginBottom: 12 }}>
                                        Dispozitive disponibile
                                    </h3>

                                    {availableDevices.length === 0 ? (
                                        <p style={{ color: '#4b7358', textAlign: 'center', padding: 20 }}>
                                            Toate dispozitivele sunt asignate
                                        </p>
                                    ) : (
                                        <div style={{
                                            display: 'grid',
                                            gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
                                            gap: 16
                                        }}>
                                            {availableDevices.map(device => (
                                                <div key={device.id} style={{
                                                    background: '#f1fcf4',
                                                    border: '1px solid #c2edd1',
                                                    borderRadius: 10,
                                                    padding: 14
                                                }}>
                                                    <strong style={{ color: '#0f3b23' }}>{device.name}</strong>
                                                    <p style={{ fontSize: 13, color: '#4b7358', margin: '6px 0' }}>
                                                        {device.description || 'Fără descriere'}
                                                    </p>
                                                    <button
                                                        onClick={() => handleAssign(device.id)}
                                                        style={{
                                                            width: '100%',
                                                            padding: 8,
                                                            border: 'none',
                                                            borderRadius: 6,
                                                            background: '#a5e9b8',
                                                            color: '#0f3b23',
                                                            cursor: 'pointer',
                                                            fontWeight: 600,
                                                            display: 'flex',
                                                            gap: 6,
                                                            justifyContent: 'center'
                                                        }}
                                                    >
                                                        <Link2 size={14} />
                                                        Asignează
                                                    </button>
                                                </div>
                                            ))}
                                        </div>
                                    )}
                                </div>

                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
