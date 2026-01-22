import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import ws from '../services/WebSocketClient';
import { MessageSquare, Send, User } from 'lucide-react';

const AdminSupport = () => {
    const { user } = useAuth();
    const [chats, setChats] = useState({}); // Organizăm mesajele pe utilizatori
    const [replies, setReplies] = useState({});

    useEffect(() => {
        if (user) {
            ws.connect(user.username, localStorage.getItem('jwt'));
            // Ascultăm cererile de suport globale
            const unsub = ws.onAdminSupport((msg) => {
                setChats(prev => ({
                    ...prev,
                    [msg.username]: [...(prev[msg.username] || []), { ...msg, type: 'incoming' }]
                }));
            });
            return () => unsub();
        }
    }, [user]);

    const handleSendReply = (clientUser) => {
        const text = replies[clientUser];
        if (!text) return;

        ws.sendAdminReply(clientUser, text); // Trimitem prin WS
        setChats(prev => ({
            ...prev,
            [clientUser]: [...(prev[clientUser] || []), { message: text, type: 'outgoing', timestamp: new Date() }]
        }));
        setReplies({ ...replies, [clientUser]: '' });
    };

    return (
        <div style={{ padding: '40px', background: '#f8fafc', minHeight: '100vh' }}>
            <h1 style={{ color: '#1e293b', marginBottom: '30px' }}>Centru Suport Clienți</h1>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                {Object.keys(chats).map(clientUser => (
                    <div key={clientUser} style={{ background: 'white', borderRadius: '16px', padding: '20px', boxShadow: '0 4px 6px rgba(0,0,0,0.05)' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '15px', borderBottom: '1px solid #eee', paddingBottom: '10px' }}>
                            <User color="#10b981" />
                            <h3 style={{ margin: 0 }}>Chat cu: {clientUser}</h3>
                        </div>
                        <div style={{ height: '200px', overflowY: 'auto', marginBottom: '15px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                            {chats[clientUser].map((m, i) => (
                                <div key={i} style={{
                                    alignSelf: m.type === 'incoming' ? 'flex-start' : 'flex-end',
                                    background: m.type === 'incoming' ? '#f1f5f9' : '#d4f4dd',
                                    padding: '8px 12px', borderRadius: '12px', maxWidth: '80%'
                                }}>
                                    {m.message}
                                </div>
                            ))}
                        </div>
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <input
                                value={replies[clientUser] || ''}
                                onChange={(e) => setReplies({ ...replies, [clientUser]: e.target.value })}
                                style={{ flex: 1, padding: '10px', borderRadius: '8px', border: '1px solid #ddd' }}
                                placeholder="Scrie un răspuns..."
                            />
                            <button onClick={() => handleSendReply(clientUser)} style={{ background: '#10b981', color: 'white', border: 'none', padding: '10px 15px', borderRadius: '8px', cursor: 'pointer' }}>
                                <Send size={18} />
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default AdminSupport;