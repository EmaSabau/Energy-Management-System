const API_BASE_URL = 'http://localhost/api';

const getAuthHeaders = () => {
    const jwt = localStorage.getItem('jwt');
    return {
        'Content-Type': 'application/json',
        ...(jwt && { 'Authorization': `Bearer ${jwt}` })
    };
};

export const authAPI = {
    login: async (username, password) => {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
            credentials: 'include'
        });
        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || 'Login failed');
        }
        return response.json();
    },

    register: async (userData) => {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData),
            credentials: 'include'
        });
        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || 'Registration failed');
        }
        return response.json();
    },

    verifyJwt: async (token) => {
        const response = await fetch(`${API_BASE_URL}/verify-jwt?token=${token}`, {
            method: 'GET',
            headers: getAuthHeaders(),
            credentials: 'include'
        });
        if (!response.ok) throw new Error('JWT is invalid or expired');
        return response.headers;
    }
};

export const userAPI = {
    getAll: async () => {
        const response = await fetch(`${API_BASE_URL}/users`, {
            headers: getAuthHeaders(),
            credentials: 'include'
        });
        if (!response.ok) throw new Error('Failed to fetch users');
        return response.json();
    },

    getByUsername: async (username) => {
        const res = await fetch(`${API_BASE_URL}/users/by-username/${username}`, {
            headers: getAuthHeaders(),
        });
        if (!res.ok) throw new Error('Failed to fetch user by username');
        return res.json();
    },

    create: async (userData) => {
        const response = await fetch(`${API_BASE_URL}/users/create-user`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(userData),
            credentials: 'include'
        });
        if (!response.ok) throw new Error('Failed to create user');
        return response.json();
    },

    update: async (id, userData) => {
        const response = await fetch(`${API_BASE_URL}/users/update-user/${id}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(userData),
            credentials: 'include'
        });
        if (!response.ok) throw new Error('Failed to update user');
        return response.json();
    },

    delete: async (id) => {
        const response = await fetch(`${API_BASE_URL}/users/delete-user/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders(),
            credentials: 'include'
        });
        if (!response.ok) throw new Error('Failed to delete user');
        return response.json();
    }
};

export const deviceAPI = {
    getAll: async () => {
        const response = await fetch(`${API_BASE_URL}/devices`, {
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Failed to fetch devices');
        return response.json();
    },

    getById: async (id) => {
        const response = await fetch(`${API_BASE_URL}/devices/${id}`, {
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Device not found');
        return response.json();
    },

    create: async (deviceData) => {
        const response = await fetch(`${API_BASE_URL}/devices`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(deviceData)
        });
        if (!response.ok) throw new Error('Failed to create device');
        return response.json();
    },

    update: async (id, deviceData) => {
        const response = await fetch(`${API_BASE_URL}/devices/${id}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(deviceData)
        });
        if (!response.ok) throw new Error('Failed to update device');
        return response.json();
    },

    delete: async (id) => {
        const response = await fetch(`${API_BASE_URL}/devices/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Failed to delete device');
        return response.json();
    },

    getUserDevices: async (ownerID) => {
        const response = await fetch(`${API_BASE_URL}/devices/user/${ownerID}`, {
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Failed to fetch user devices');
        return response.json();
    },

    assignToUser: async (deviceId, ownerID) => {
        const response = await fetch(`${API_BASE_URL}/devices/${deviceId}/assign/${ownerID}`, {
            method: 'POST',
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Failed to assign device');
        return response.json();
    },

    unassignFromUser: async (deviceId) => {
        const response = await fetch(`${API_BASE_URL}/devices/${deviceId}/unassign`, {
            method: 'POST',
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Failed to unassign device');
        return response.json();
    }
};

export const monitoringAPI = {
    getDaily: async (deviceId, date) => {
        const response = await fetch(
            `${API_BASE_URL}/monitoring/device/${deviceId}/daily?date=${date}`,
            {
                headers: getAuthHeaders(),
                credentials: 'include'
            }
        );
        if (!response.ok) throw new Error('Failed to fetch consumption data');
        return response.json();
    }
};

export const notificationAPI = {
    sendToUser: async (username, notification) => {
        const response = await fetch(
            `${API_BASE_URL}/notifications/send/${username}`,
            {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(notification),
                credentials: 'include'
            }
        );

        if (!response.ok) {
            const err = await response.text();
            throw new Error(err || 'Failed to send notification');
        }

        return response.text();
    },

    broadcast: async (notification) => {
        const response = await fetch(
            `${API_BASE_URL}/notifications/broadcast`,
            {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(notification),
                credentials: 'include'
            }
        );

        if (!response.ok) {
            const err = await response.text();
            throw new Error(err || 'Failed to broadcast notification');
        }

        return response.text();
    },

    health: async () => {
        const response = await fetch(
            `${API_BASE_URL}/notifications/health`,
            {
                headers: getAuthHeaders()
            }
        );

        if (!response.ok) throw new Error('WebSocket service down');
        return response.text();
    }
};
export const chatAPI = {
    sendMessage: async (chatMessage) => {
        const response = await fetch(`${API_BASE_URL}/chat/message`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(chatMessage),
            credentials: 'include'
        });

        if (!response.ok) {
            const err = await response.text();
            throw new Error(err || 'Failed to send chat message');
        }

        return response.json();
    },

    health: async () => {
        const response = await fetch(`${API_BASE_URL}/chat/health`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) {
            throw new Error('Chat service unavailable');
        }

        return response.text();
    }
};

