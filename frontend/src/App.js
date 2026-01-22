import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import NotificationCenter from './components/NotificationCenter';
import LoginPage from './pages/LoginPage';
import UserManagement from './pages/UserManagement';
import DeviceManagement from './pages/DeviceManagement';
import AssignDevices from './pages/AssignDevices';
import ClientDashboard from './pages/ClientDashboard';
import DeviceDetails from "./pages/DeviceDetails";
import ChatWidget from "./components/ChatWidget";
import AdminSupport from "./pages/AdminSupport";
import { Outlet } from 'react-router-dom';

function LayoutWithNavbar() {
    const { user } = useAuth();

    return (
        <div style={{ minHeight: '100vh', position: 'relative', display: 'flex', flexDirection: 'column' }}>
            <Navbar />
            <main style={{ flex: 1 }}>
                <Outlet />
            </main>
            {user && (
                <>
                    <NotificationCenter userId={user.id} />
                </>
            )}
        </div>
    );
}

function AppRoutes() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route element={<LayoutWithNavbar />}>
                {/* Client */}
                <Route path="/devices" element={<ClientDashboard />} />
                <Route path="/dashboard" element={<ClientDashboard />} />

                {/* Admin */}
                <Route path="/admin/dashboard" element={<DeviceManagement />} />
                <Route path="/admin/users" element={<UserManagement />} />
                <Route path="/admin/devices" element={<DeviceManagement />} />
                <Route path="/admin/assign" element={<AssignDevices />} />
                <Route path="/device/:id" element={<DeviceDetails />} />
                <Route path="/admin/support" element={<AdminSupport />} />

                {/* Default */}
                <Route path="/" element={<Navigate to="/login" replace />} />
                <Route path="*" element={<Navigate to="/login" replace />} />
            </Route>
        </Routes>
    );
}

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <AppRoutes />
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;