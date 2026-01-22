import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    const normalizeRole = (role) => {
        if (!role) return 'client';
        const normalizedRole = role.toUpperCase();
        if (normalizedRole.includes('ADMIN')) return 'admin';
        return 'client';
    };

    const extractUserData = (token) => {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const username = payload.username || payload.sub || payload.name;

        // Salvează pentru WebSocketClient
        localStorage.setItem('username', username);

        let userRole = 'client';
        if (payload.role) userRole = normalizeRole(payload.role);
        else if (payload.roles) userRole = payload.roles.some(r => normalizeRole(r) === 'admin') ? 'admin' : 'client';
        else if (payload.authorities) userRole = payload.authorities.some(a => normalizeRole(typeof a === 'string' ? a : a.authority) === 'admin') ? 'admin' : 'client';

        return {
            id: payload.id || payload.userId || payload.sub,
            username: username,
            role: userRole
        };
    };

    useEffect(() => {
        const jwt = localStorage.getItem('jwt');
        if (jwt) {
            try {
                setUser(extractUserData(jwt));
            } catch (error) {
                console.error('Error decoding JWT:', error);
                localStorage.removeItem('jwt');
                localStorage.removeItem('username');
            }
        }
        setLoading(false);
    }, []);

    const login = (token) => {
        localStorage.setItem('jwt', token);
        setUser(extractUserData(token));
    };

    const logout = () => {
        setUser(null);
        localStorage.removeItem('jwt');
        localStorage.removeItem('username');
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, isAdmin: () => user?.role === 'admin', isClient: () => user?.role === 'client', loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);