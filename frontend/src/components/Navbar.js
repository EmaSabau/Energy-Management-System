import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogOut, Home, Users, Zap, Link2, Headset } from 'lucide-react'; // Am adăugat Headset

export default function Navbar() {
    const { user, logout, isAdmin } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const isActive = (path) => location.pathname === path;

    const navLinkStyle = (path) => ({
        display: 'flex',
        alignItems: 'center',
        gap: '6px',
        padding: '8px 14px',
        borderRadius: '10px',
        textDecoration: 'none',
        fontWeight: '600',
        fontSize: '15px',
        transition: '0.2s',
        background: isActive(path) ? '#f6f6f6' : 'transparent',
        color: '#2f4a3a'
    });

    return (
        <nav style={{
            background: 'white',
            padding: '14px 0',
            borderBottom: '1px solid #eee'
        }}>
            <div
                style={{
                    maxWidth: '1400px',
                    margin: '0 auto',
                    padding: '0 40px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                }}
            >
                {/* LOGO */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <div
                        style={{
                            width: '42px',
                            height: '42px',
                            background: '#b7d6a2',
                            borderRadius: '10px',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center'
                        }}
                    >
                        <Zap size={22} color="#475c45" />
                    </div>
                    <span
                        style={{
                            fontSize: '22px',
                            fontWeight: '800',
                            color: '#2f4a3a',
                            letterSpacing: '1px',
                        }}
                    >
                        DeviceHub
                    </span>
                </div>

                {/* NAV LINKS */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    {isAdmin() ? (
                        <>
                            <Link to="/admin/devices" style={navLinkStyle('/admin/devices')}>
                                <Zap size={16} />
                                Dispozitive
                            </Link>
                            <Link to="/admin/users" style={navLinkStyle('/admin/users')}>
                                <Users size={16} />
                                Utilizatori
                            </Link>
                            <Link to="/admin/assign" style={navLinkStyle('/admin/assign')}>
                                <Link2 size={16} />
                                Asignare
                            </Link>
                            {/* LINK NOU: Pagina de Suport Admin */}
                            <Link to="/admin/support" style={navLinkStyle('/admin/support')}>
                                <Headset size={16} />
                                Suport
                            </Link>
                        </>
                    ) : (
                        <Link to="/dashboard" style={navLinkStyle('/dashboard')}>
                            <Home size={16} />
                            Dashboard
                        </Link>
                    )}
                </div>

                {/* USER SECTION */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
                    <div
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '10px',
                            padding: '8px 14px',
                            background: '#f6f6f6',
                            borderRadius: '10px'
                        }}
                    >
                        <div
                            style={{
                                width: '32px',
                                height: '32px',
                                background: '#b7d6a2',
                                borderRadius: '50%',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                color: '#475c45',
                                fontWeight: '700'
                            }}
                        >
                            {user?.username?.charAt(0).toUpperCase()}
                        </div>

                        <div style={{ lineHeight: '1.1' }}>
                            <div
                                style={{
                                    fontSize: '14px',
                                    fontWeight: '700',
                                    color: '#475c45'
                                }}
                            >
                                {user?.username}
                            </div>
                            <div
                                style={{
                                    fontSize: '12px',
                                    color: '#475c45'
                                }}
                            >
                                {isAdmin() ? 'Administrator' : 'Client'}
                            </div>
                        </div>
                    </div>

                    <button
                        onClick={handleLogout}
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '6px',
                            padding: '8px 16px',
                            borderRadius: '10px',
                            background: '#2f4a3a',
                            color: 'white',
                            border: 'none',
                            fontWeight: '600',
                            cursor: 'pointer',
                            transition: '0.2s'
                        }}
                    >
                        <LogOut size={16} />
                        Logout
                    </button>
                </div>
            </div>
        </nav>
    );
}