import React, { useState, useEffect } from 'react';
import { deviceAPI } from '../services/api';
import { Zap, Edit, Trash2, Plus, X, Search } from 'lucide-react';

export default function DeviceManagement() {
    const [devices, setDevices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [editingDevice, setEditingDevice] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        address: '',
        maxConsumption: '',
        imageUrl: ''
    });

    useEffect(() => {
        fetchDevices();
    }, []);

    const fetchDevices = async () => {
        try {
            const data = await deviceAPI.getAll();
            setDevices(data);
        } catch {
            alert('Eroare la încărcarea dispozitivelor');
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingDevice) {
                await deviceAPI.update(editingDevice.id, formData);
            } else {
                await deviceAPI.create(formData);
            }
            setShowModal(false);
            setFormData({ name: '', description: '', address: '', maxConsumption: '', imageUrl: '' });
            setEditingDevice(null);
            fetchDevices();
        } catch (error) {
            alert('Eroare: ' + error.message);
        }
    };

    const handleEdit = (device) => {
        setEditingDevice(device);
        setFormData({
            name: device.name,
            description: device.description || '',
            address: device.address || '',
            maxConsumption: device.maxConsumption || '',
            imageUrl: device.imageUrl || ''
        });
        setShowModal(true);
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Ștergi dispozitivul?')) return;

        await deviceAPI.delete(id);
        fetchDevices();
    };

    const closeModal = () => {
        setShowModal(false);
        setEditingDevice(null);
        setFormData({ name: '', description: '', address: '', maxConsumption: '', imageUrl: '' });
    };

    const filteredDevices = devices.filter(d =>
        d.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (d.description || '').toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) return <div style={{ padding: '40px', textAlign: 'center' }}>Se încarcă...</div>;

    return (
        <div style={{ minHeight: '100vh', padding: '40px', background: '#f8fafc' }}>
            <div style={{ maxWidth: '1500px', margin: '0 auto' }}>

                {/* SEARCH + ADD */}
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: '20px', marginBottom: '30px' }}>
                    <div style={{ flex: 1, position: 'relative', maxWidth: '600px' }}>
                        <Search
                            size={20}
                            style={{
                                position: 'absolute',
                                top: '50%',
                                left: '16px',
                                transform: 'translateY(-50%)',
                                color: '#64748b'
                            }}
                        />
                        <input
                            type="text"
                            placeholder="Caută dispozitive..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            style={{
                                width: '100%',
                                padding: '14px 16px 14px 44px',
                                border: '2px solid #cbd5e1',
                                borderRadius: '24px',
                                outline: 'none'
                            }}
                        />
                    </div>

                    <button
                        onClick={() => setShowModal(true)}
                        style={{
                            background: '#067a00',
                            color: 'white',
                            border: 'none',
                            borderRadius: '12px',
                            padding: '14px 24px',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '10px',
                            fontWeight: '600',
                            cursor: 'pointer'
                        }}
                    >
                        <Plus size={20} /> Adaugă
                    </button>
                </div>

                {/* MAIN CONTAINER */}
                <div style={{
                    background: '#d4f4dd',
                    borderRadius: '24px',
                    padding: '40px'
                }}>
                    <h1 style={{ fontSize: '40px', fontWeight: '800', color: '#1e293b', marginBottom: '30px' }}>
                        Dispozitive
                    </h1>

                    {/* GRID */}
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
                        gap: '24px'
                    }}>
                        {filteredDevices.map(device => (
                            <div
                                key={device.id}
                                style={{
                                    background: 'white',
                                    borderRadius: '16px',
                                    padding: '24px',
                                    boxShadow: '0 4px 12px rgba(0,0,0,0.08)'
                                }}
                            >
                                <h3 style={{ textAlign: 'center', fontWeight: 600, marginBottom: '14px' }}>
                                    {device.name}
                                </h3>

                                <div style={{
                                    background: '#f1f5f9',
                                    borderRadius: '14px',
                                    height: '180px',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    marginBottom: '16px'
                                }}>
                                    {device.imageUrl ? (
                                        <img
                                            src={device.imageUrl}
                                            alt={device.name}
                                            style={{
                                                maxWidth: '100%',
                                                maxHeight: '100%',
                                                objectFit: 'contain',
                                                padding: '10px'
                                            }}
                                        />
                                    ) : (
                                        <Zap size={50} color="#94a3b8" />
                                    )}
                                </div>

                                <p style={{ textAlign: 'center', color: '#64748b', minHeight: '40px' }}>
                                    {device.description || 'Fără descriere'}
                                </p>

                                {/* TAGS */}
                                <div style={{ display: 'flex', justifyContent: 'center', gap: '8px', flexWrap: 'wrap' }}>
                                    {device.address && (
                                        <span style={{
                                            background: '#d4f4dd',
                                            color: '#067a00',
                                            padding: '6px 10px',
                                            borderRadius: '12px',
                                            fontSize: '12px',
                                            fontWeight: '600'
                                        }}>
                                            {device.address}
                                        </span>
                                    )}
                                    {device.maxConsumption && (
                                        <span style={{
                                            background: '#e8f8ee',
                                            color: '#067a00',
                                            padding: '6px 10px',
                                            borderRadius: '12px',
                                            fontSize: '12px',
                                            fontWeight: '600'
                                        }}>
                                            {device.maxConsumption}W
                                        </span>
                                    )}
                                </div>

                                {/* ACTIONS */}
                                <div style={{ display: 'flex', gap: '10px', marginTop: '18px' }}>
                                    <button
                                        onClick={() => handleEdit(device)}
                                        style={{
                                            flex: 1,
                                            background: '#d4f4dd',
                                            color: '#067a00',
                                            border: 'none',
                                            borderRadius: '10px',
                                            padding: '10px',
                                            fontWeight: '600',
                                            cursor: 'pointer'
                                        }}
                                    >
                                        <Edit size={16} /> Edit
                                    </button>

                                    <button
                                        onClick={() => handleDelete(device.id)}
                                        style={{
                                            flex: 1,
                                            background: '#b8e6c2',
                                            color: '#045a00',
                                            border: 'none',
                                            borderRadius: '10px',
                                            padding: '10px',
                                            fontWeight: '600',
                                            cursor: 'pointer'
                                        }}
                                    >
                                        <Trash2 size={16} /> Șterge
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* EMPTY STATE */}
                    {filteredDevices.length === 0 && (
                        <div style={{ textAlign: 'center', padding: '60px', color: '#64748b' }}>
                            <Zap size={50} color="#94a3b8" />
                            <h3>Niciun dispozitiv găsit</h3>
                        </div>
                    )}
                </div>
            </div>

            {/* MODAL */}
            {showModal && (
                <div style={{
                    position: 'fixed',
                    inset: 0,
                    background: 'rgba(0,0,0,0.4)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                }}>
                    <div style={{
                        background: 'white',
                        padding: '32px',
                        borderRadius: '20px',
                        width: '500px',
                        position: 'relative'
                    }}>
                        <button
                            onClick={closeModal}
                            style={{
                                position: 'absolute',
                                top: 16,
                                right: 16,
                                background: '#eef2f5',
                                border: 'none',
                                borderRadius: '8px',
                                width: '32px',
                                height: '32px',
                                cursor: 'pointer'
                            }}
                        >
                            <X size={18} />
                        </button>

                        <h2 style={{ fontWeight: '700', marginBottom: '20px' }}>
                            {editingDevice ? 'Editează Dispozitiv' : 'Adaugă Dispozitiv'}
                        </h2>

                        <form onSubmit={handleSubmit}>
                            {['name', 'description', 'address', 'maxConsumption', 'imageUrl'].map(field => (
                                <div key={field} style={{ marginBottom: '16px' }}>
                                    <label style={{ display: 'block', fontWeight: '600', marginBottom: '6px' }}>
                                        {field === 'name' ? 'Nume *' :
                                            field === 'description' ? 'Descriere' :
                                                field === 'address' ? 'Adresă' :
                                                    field === 'maxConsumption' ? 'Consum Max (W)' :
                                                        'URL Imagine'}
                                    </label>

                                    {field === 'description' ? (
                                        <textarea
                                            rows="3"
                                            value={formData[field]}
                                            onChange={e => setFormData({ ...formData, [field]: e.target.value })}
                                            style={inputStyle}
                                        />
                                    ) : (
                                        <input
                                            type={field === 'maxConsumption' ? 'number' : 'text'}
                                            value={formData[field]}
                                            onChange={e => setFormData({ ...formData, [field]: e.target.value })}
                                            required={field === 'name'}
                                            style={inputStyle}
                                        />
                                    )}
                                </div>
                            ))}

                            <button
                                type="submit"
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    background: '#067a00',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '10px',
                                    cursor: 'pointer',
                                    fontWeight: '600'
                                }}
                            >
                                {editingDevice ? 'Actualizează' : 'Creează'}
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
    padding: '12px',
    border: '2px solid #cbd5e1',
    borderRadius: '10px',
    outline: 'none'
};
