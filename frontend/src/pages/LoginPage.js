import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './Login.css';

export default function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [isLogin, setIsLogin] = useState(true);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async () => {
        setError('');
        setLoading(true);

        try {
            const url = isLogin
                ? 'http://localhost/api/auth/login'
                : 'http://localhost/api/auth/register';

            const requestBody = isLogin
                ? { username, password }
                : { username, password, email, role: 'ROLE_CLIENT' }; // Default role for registration

            const response = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(requestBody),
                credentials: 'include'
            });

            if (!response.ok) {
                let errorMessage = 'Eroare la autentificare';
                try {
                    const data = await response.json();
                    errorMessage = data.message || errorMessage;
                } catch (err) {
                    console.warn('Body-ul răspunsului nu e JSON valid');
                }
                throw new Error(errorMessage);
            }

            const data = await response.json();

            if (isLogin) {
                localStorage.setItem('jwt', data.token);
                login(data.token);
                try {
                    const payload = JSON.parse(atob(data.token.split('.')[1]));
                    const userRole = payload.role || payload.roles || payload.authorities;
                    const isAdmin = typeof userRole === 'string'
                        ? userRole.toUpperCase().includes('ADMIN')
                        : Array.isArray(userRole) && userRole.some(r => {
                        const role = typeof r === 'string' ? r : r.authority;
                        return role.toUpperCase().includes('ADMIN');
                    });

                    navigate(isAdmin ? '/admin/dashboard' : '/devices', { replace: true });
                } catch (err) {
                    console.error('Error decoding token:', err);
                    navigate('/devices');
                }
            } else {
                alert(`Înregistrare reușită pentru: ${username}`);
                setIsLogin(true);
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-page">
            <img
                src={require('../assets/loginRegister.png')}
                alt="Background"
                className="background-image"
            />

            <div className="login-overlay">
                <div className="login-box">
                    <div className="logo-circle">
                        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                            <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4M10 17l5-5-5-5M13.8 12H3"/>
                        </svg>
                    </div>

                    <h1>{isLogin ? 'Bun venit!' : 'Creează cont'}</h1>
                    <p className="subtitle">{isLogin ? 'Conectează-te pentru a continua' : 'Înregistrează-te pentru a începe'}</p>

                    {error && (
                        <div style={{
                            background: '#fee',
                            color: '#2f4a3a',
                            padding: '12px',
                            borderRadius: '6px',
                            marginBottom: '16px',
                            fontSize: '14px',
                            textAlign: 'center'
                        }}>
                            {error}
                        </div>
                    )}

                    <div className="form-group">
                        <label>Nume utilizator</label>
                        <input
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="Introdu username"
                            disabled={loading}
                        />
                    </div>
                    {!isLogin && (
                        <div className="form-group">
                            <label>Email</label>
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="Introdu email-ul"
                                disabled={loading}
                            />
                        </div>
                    )}

                    <div className="form-group">
                        <label>Parolă</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Introdu parola"
                            disabled={loading}
                        />
                    </div>

                    <button
                        onClick={handleSubmit}
                        className="login-button"
                        disabled={loading || !username || !password}
                    >
                        {loading ? (isLogin ? 'Se autentifică...' : 'Se înregistrează...') : (isLogin ? 'Autentifică-te' : 'Înregistrează-te')}
                    </button>

                    <div className="register-link">
                        <span>{isLogin ? 'Nu ai cont?' : 'Ai deja cont?'} </span>
                        <button onClick={() => setIsLogin(!isLogin)} disabled={loading}>
                            {isLogin ? 'Înregistrează-te' : 'Autentifică-te'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}