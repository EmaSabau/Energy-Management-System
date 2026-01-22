import { useEffect, useState, useCallback } from "react";
import { useParams } from "react-router-dom";
import { monitoringAPI, deviceAPI } from "../services/api";
import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from 'recharts';

export default function DeviceDetails() {
    const { id } = useParams();
    const [device, setDevice] = useState(null);
    // Inițializăm data cu fusul orar local pentru a evita decalajele UTC
    const [date, setDate] = useState(() => {
        const now = new Date();
        const offset = now.getTimezoneOffset() * 60000;
        return new Date(now - offset).toISOString().split("T")[0];
    });
    const [data, setData] = useState([]);
    const [loadingDevice, setLoadingDevice] = useState(true);
    const [loadingData, setLoadingData] = useState(false);
    const [error, setError] = useState(null);

    // Folosim useCallback pentru a ne asigura că funcția nu este recreată inutil
    const loadConsumption = useCallback(async (deviceId, targetDate) => {
        setLoadingData(true);
        try {
            const res = await monitoringAPI.getDaily(deviceId, targetDate);
            setData(res || []);
            if (res && res.length > 0) {
                setError(null);
            } else {
                setError("Nu există date de consum pentru această dată.");
            }
        } catch (err) {
            console.error("Failed fetching consumption:", err);
            setData([]);
            setError("Nu există date de consum pentru această dată.");
        } finally {
            setLoadingData(false);
        }
    }, []);

    // 1. Încărcăm metadatele dispozitivului și declanșăm imediat prima încărcare de date
    useEffect(() => {
        const init = async () => {
            setLoadingDevice(true);
            try {
                const d = await deviceAPI.getById(id);
                setDevice(d);
                // Imediat ce avem device-ul, încărcăm și consumul pentru data curentă
                await loadConsumption(id, date);
            } catch (err) {
                console.error("Failed fetching device:", err);
                setError("Eroare la încărcarea dispozitivului.");
            } finally {
                setLoadingDevice(false);
            }
        };
        if (id) init();
    }, [id, loadConsumption]); // loadConsumption este stabil datorită useCallback

    // 2. Efect separat pentru când utilizatorul schimbă manual data din picker
    useEffect(() => {
        if (device && id) {
            loadConsumption(id, date);
        }
    }, [date, id, device, loadConsumption]);

    if (loadingDevice) return <div style={{ padding: "40px" }}>Se încarcă dispozitivul...</div>;
    if (!device) return <div style={{ padding: "40px" }}>Dispozitivul nu a fost găsit.</div>;

    return (
        <div style={{ padding: "40px" }}>
            <h1 style={{ fontSize: "32px", marginBottom: "10px" }}>{device.name}</h1>
            <p style={{ marginBottom: "25px", color: "#64748b" }}>{device.description || "Fără descriere"}</p>

            <div style={{ marginBottom: "30px", display: "flex", alignItems: "center", gap: "10px" }}>
                <label style={{ fontSize: "16px", fontWeight: 600 }}>Alege ziua:</label>
                <input
                    type="date"
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                    style={{ padding: "8px 12px", borderRadius: "8px", border: "1px solid #ccc", fontSize: "16px" }}
                />
                {loadingData && <span style={{ marginLeft: "10px", color: "#64748b" }}>Se actualizează...</span>}
            </div>

            <div style={{
                width: "100%",
                height: "450px",
                background: "white",
                borderRadius: "16px",
                padding: "20px",
                boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
                position: "relative"
            }}>
                {data && data.length > 0 ? (
                    <ResponsiveContainer key={`${id}-${date}-${data.length}`}>
                        <LineChart data={data}>
                            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                            <XAxis
                                dataKey="hour"
                                axisLine={false}
                                tickLine={false}
                                tick={{ fill: '#64748b' }}
                                label={{ value: 'Ora', position: 'insideBottom', offset: -10, fill: '#64748b' }}
                            />
                            <YAxis
                                axisLine={false}
                                tickLine={false}
                                tick={{ fill: '#64748b' }}
                                label={{ value: 'kWh', angle: -90, position: 'insideLeft', fill: '#64748b' }}
                            />
                            <Tooltip contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }} />
                            <Line
                                type="monotone"
                                dataKey="value"
                                stroke="#22c55e"
                                strokeWidth={4}
                                dot={{ r: 4, fill: '#22c55e' }}
                                activeDot={{ r: 6 }}
                                name="Consum (kWh)"
                                isAnimationActive={true}
                            />
                        </LineChart>
                    </ResponsiveContainer>
                ) : (
                    <div style={{ display: "flex", alignItems: "center", justifyContent: "center", height: "100%", color: "#64748b" }}>
                        {loadingData ? "Se încarcă datele..." : (error || "Nu există date de consum pentru această dată.")}
                    </div>
                )}
            </div>
        </div>
    );
}