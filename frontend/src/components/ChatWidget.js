import { useEffect, useState, useRef } from 'react';
import { useAuth } from '../context/AuthContext';
import ws from '../services/WebSocketClient';
import {
    MessageCircle, Send, X, Bot, Sparkles, User, Headset
} from 'lucide-react';
import './ChatWidget.css';

const ChatWidget = () => {
    const { user, isAdmin } = useAuth();
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [isTyping, setIsTyping] = useState(false);
    const [adminMode, setAdminMode] = useState(false);
    const messagesEndRef = useRef(null);
    const inputRef = useRef(null);

    useEffect(() => {
        if (user && !isAdmin() && isOpen) {
            const jwt = localStorage.getItem('jwt');
            ws.connect(user.username, jwt);

            const unsubscribeChat = ws.onChat((response) => {
                setIsTyping(false);
                setMessages(prev => [...prev, {
                    sender: response.sender || 'bot',
                    text: response.message,
                    timestamp: new Date(),
                    ai: response.ai_generated,
                    isFromUser: false // IMPORTANT: Marchează că mesajul vine de la server
                }]);
            });

            if (messages.length === 0) {
                setMessages([{
                    sender: 'bot',
                    text: "Salut! Sunt asistentul tău AI. Cum te pot ajuta astăzi?",
                    timestamp: new Date(),
                    isFromUser: false
                }]);
            }

            return () => unsubscribeChat();
        }
    }, [user, isAdmin, isOpen]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, isTyping]);

    const handleSend = () => {
        if (!input.trim()) return;

        const newMessage = {
            sender: user.username,
            text: input,
            timestamp: new Date(),
            ai: false,
            isFromUser: true // IMPORTANT: Marchează că mesajul e trimis de user
        };

        setMessages(prev => [...prev, newMessage]);

        if (adminMode) {
            ws.sendAdminChat(user.username, input);
        } else {
            setIsTyping(true);
            ws.sendChat(user.username, input);
        }

        setInput('');
    };

    return (
        <div className="chat-widget-wrapper">
            {isOpen && (
                <div className="chat-main-container">
                    <div className="chat-header-styled">
                        <div className="header-info">
                            <div className={`avatar-circle ${adminMode ? 'admin-active' : ''}`}>
                                {adminMode ? <Headset size={20} /> : <Sparkles size={20} />}
                            </div>
                            <div>
                                <h4>{adminMode ? "Suport Admin" : "AI Assistant"}</h4>
                                <span className="status-text">Online</span>
                            </div>
                        </div>
                        <div className="header-actions">
                            {!isAdmin() && (
                                <button
                                    className={`mode-toggle ${adminMode ? 'active-mode-btn' : ''}`}
                                    onClick={() => setAdminMode(!adminMode)}
                                    title={adminMode ? "Treci la AI" : "Cere ajutor Admin"}
                                >
                                    {adminMode ? <Bot size={18} /> : <Headset size={18} />}
                                </button>
                            )}
                            <button className="close-btn" onClick={() => setIsOpen(false)}>
                                <X size={20} />
                            </button>
                        </div>
                    </div>

                    <div className="chat-content-styled">
                        {messages.map((msg, idx) => {
                            // Folosim flag-ul isFromUser pentru a determina direcția
                            const isUserMessage = msg.isFromUser === true;

                            return (
                                <div key={idx} className={`message-row ${isUserMessage ? 'user' : 'bot'}`}>
                                    <div className="msg-avatar-small">
                                        {isUserMessage ? (
                                            <User size={12} />
                                        ) : (
                                            msg.ai ? <Sparkles size={12} /> : <Bot size={12} />
                                        )}
                                    </div>
                                    <div className="msg-bubble">
                                        {msg.text}
                                        <span className="msg-time">
                                            {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                        </span>
                                    </div>
                                </div>
                            );
                        })}
                        {isTyping && (
                            <div className="message-row bot">
                                <div className="msg-avatar-small"><Bot size={12} /></div>
                                <div className="typing-dots">
                                    <span></span><span></span><span></span>
                                </div>
                            </div>
                        )}
                        <div ref={messagesEndRef} />
                    </div>

                    <div className="chat-footer-styled">
                        <div className="input-row">
                            <textarea
                                ref={inputRef}
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                                onKeyDown={(e) => e.key === 'Enter' && !e.shiftKey && (e.preventDefault(), handleSend())}
                                placeholder={adminMode ? "Mesaj către admin..." : "Scrie un mesaj..."}
                                rows="1"
                            />
                            <button className="send-styled-btn" onClick={handleSend} disabled={!input.trim()}>
                                <Send size={18} />
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <button className="chat-trigger-btn" onClick={() => setIsOpen(!isOpen)}>
                {isOpen ? <X size={24} /> : <MessageCircle size={24} />}
                <span>{adminMode ? "Mod Admin" : "Asistent"}</span>
            </button>
        </div>
    );
};

export default ChatWidget;