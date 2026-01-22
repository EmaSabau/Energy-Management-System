import React, { useState, useEffect } from 'react';
import { userAPI } from '../services/api';
import { Edit, Trash2, Plus, X } from 'lucide-react';

export default function UserManagement() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [editingUser, setEditingUser] = useState(null);
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        role: 'ROLE_CLIENT'
    });

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const data = await userAPI.getAll();
            setUsers(data);
        } catch (error) {
            alert('Eroare la încărcarea utilizatorilor');
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingUser) {
                await userAPI.update(editingUser.id, formData);
                alert('Utilizator actualizat!');
            } else {
                await userAPI.create(formData);
                alert('Utilizator creat!');
            }
            setShowModal(false);
            setEditingUser(null);
            setFormData({ username: '', email: '', role: 'ROLE_CLIENT' });
            fetchUsers();
        } catch (error) {
            alert('Eroare: ' + error.message);
        }
    };

    const handleEdit = (user) => {
        setEditingUser(user);
        setFormData({
            username: user.username,
            email: user.email || '',
            role: Array.isArray(user.role) ? user.role[0] : user.role
        });
        setShowModal(true);
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Sigur ștergi utilizatorul?')) return;
        try {
            await userAPI.delete(id);
            alert('Șters!');
            fetchUsers();
        } catch (error) {
            alert('Eroare la ștergere: ' + error.message);
        }
    };

    const closeModal = () => {
        setShowModal(false);
        setEditingUser(null);
        setFormData({ username: '', email: '', password: '', role: 'ROLE_CLIENT' });
    };

    if (loading) return <div style={{ padding: '40px', textAlign: 'center' }}>Se încarcă...</div>;

    return (
        <div style={{ padding: '40px', maxWidth: '1200px', margin: '0 auto' }}>

            {/* HEADER */}
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '30px' }}>
                <h1 style={{ fontSize: '32px', fontWeight: '800', color: '#1e293b', margin: 0 }}>
                    Gestionare Utilizatori
                </h1>

                <button
                    onClick={() => setShowModal(true)}
                    style={{
                        padding: '10px 16px',
                        background: '#067a00',
                        color: 'white',
                        border: 'none',
                        borderRadius: '10px',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '8px',
                        fontWeight: '600'
                    }}
                >
                    <Plus size={18} /> Adaugă
                </button>
            </div>

            {/* TABLE */}
            <table style={{
                width: '100%',
                background: 'white',
                borderRadius: '16px',
                overflow: 'hidden',
                boxShadow: '0 2px 12px rgba(0,0,0,0.08)'
            }}>
                <thead style={{ background: '#f1f5f9', fontWeight: '600' }}>
                <tr>
                    <th style={{ padding: '14px', textAlign: 'left' }}>ID</th>
                    <th style={{ padding: '14px', textAlign: 'left' }}>Username</th>
                    <th style={{ padding: '14px', textAlign: 'left' }}>Email</th>
                    <th style={{ padding: '14px', textAlign: 'left' }}>Rol</th>
                    <th style={{ padding: '14px', textAlign: 'center' }}>Acțiuni</th>
                </tr>
                </thead>

                <tbody>
                {users.map(user => (
                    <tr key={user.id} style={{ borderBottom: '1px solid #e2e8f0' }}>
                        <td style={{ padding: '12px' }}>{user.id}</td>
                        <td style={{ padding: '12px' }}>{user.username}</td>
                        <td style={{ padding: '12px' }}>{user.email || '-'}</td>

                        <td style={{ padding: '12px' }}>
                            <span style={{
                                padding: '5px 12px',
                                borderRadius: '20px',
                                fontSize: '13px',
                                fontWeight: '600',
                                background:
                                    Array.isArray(user.role) && user.role.includes('ROLE_ADMIN')
                                        ? '#ffdddd'
                                        : '#d4f4dd',
                                color:
                                    Array.isArray(user.role) && user.role.includes('ROLE_ADMIN')
                                        ? '#c00'
                                        : '#067a00'
                            }}>
                                {Array.isArray(user.role) && user.role.includes('ROLE_ADMIN') ? 'Admin' : 'Client'}
                            </span>
                        </td>

                        <td style={{ padding: '12px', textAlign: 'center' }}>
                            <button
                                onClick={() => handleEdit(user)}
                                style={{
                                    marginRight: '8px',
                                    background: '#d4f4dd',
                                    border: 'none',
                                    padding: '6px 8px',
                                    borderRadius: '6px',
                                    cursor: 'pointer'
                                }}
                            >
                                <Edit size={18} color="#067a00" />
                            </button>

                            <button
                                onClick={() => handleDelete(user.id)}
                                style={{
                                    background: '#ffdddd',
                                    border: 'none',
                                    padding: '6px 8px',
                                    borderRadius: '6px',
                                    cursor: 'pointer'
                                }}
                            >
                                <Trash2 size={18} color="#c00" />
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {/* MODAL */}
            {showModal && (
                <div style={{
                    position: 'fixed', inset: 0,
                    background: 'rgba(0,0,0,0.4)',
                    display: 'flex', justifyContent: 'center', alignItems: 'center'
                }}>
                    <div style={{
                        background: 'white',
                        padding: '30px',
                        width: '380px',
                        borderRadius: '16px',
                        position: 'relative',
                        boxShadow: '0 6px 20px rgba(0,0,0,0.15)'
                    }}>
                        <button
                            onClick={closeModal}
                            style={{
                                border: 'none',
                                background: 'none',
                                cursor: 'pointer',
                                position: 'absolute',
                                right: 12, top: 12
                            }}
                        >
                            <X size={22} color="#64748b" />
                        </button>

                        <h2 style={{ marginBottom: '20px', fontWeight: '700', color: '#1e293b' }}>
                            {editingUser ? 'Editează Utilizator' : 'Adaugă Utilizator'}
                        </h2>

                        <form onSubmit={handleSubmit}>
                            <label>Username</label>
                            <input
                                required
                                value={formData.username}
                                onChange={e => setFormData({ ...formData, username: e.target.value })}
                                style={inputStyle}
                            />

                            <label>Email</label>
                            <input
                                value={formData.email}
                                onChange={e => setFormData({ ...formData, email: e.target.value })}
                                style={inputStyle}
                            />

                            <label>Rol</label>
                            <select
                                value={formData.role}
                                onChange={e => setFormData({ ...formData, role: e.target.value })}
                                style={{ ...inputStyle, cursor: 'pointer' }}
                            >
                                <option value="ROLE_CLIENT">Client</option>
                                <option value="ROLE_ADMIN">Admin</option>
                            </select>

                            <button type="submit" style={{
                                width: '100%',
                                padding: '12px',
                                background: '#067a00',
                                color: 'white',
                                border: 'none',
                                borderRadius: '10px',
                                cursor: 'pointer',
                                fontWeight: '600',
                                marginTop: '5px'
                            }}>
                                {editingUser ? 'Actualizează' : 'Creează'}
                            </button>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

const inputStyle = {
    width: '100%',
    padding: '10px 12px',
    marginBottom: '12px',
    borderRadius: '8px',
    border: '1px solid #cbd5e1',
};
