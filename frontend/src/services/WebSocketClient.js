import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

// Asigură-te că URL-ul corespunde configurației Traefik sau backend-ului tău direct
const WS_URL = 'http://localhost/ws';

class WebSocketClient {
    constructor() {
        this.client = null;
        this.connected = false;
        this.connecting = false;
        this.notificationCallbacks = new Set();
        this.chatCallbacks = new Set();
        this.adminSupportCallbacks = new Set();
    }

    // Înregistrare callback-uri
    onNotification(cb) { this.notificationCallbacks.add(cb); return () => this.notificationCallbacks.delete(cb); }
    onChat(cb) { this.chatCallbacks.add(cb); return () => this.chatCallbacks.delete(cb); }
    onAdminSupport(cb) { this.adminSupportCallbacks.add(cb); return () => this.adminSupportCallbacks.delete(cb); }

    connect(username, jwt) {
        if (this.connected || this.connecting || !jwt || !username) return;

        this.connecting = true;
        const socket = new SockJS(WS_URL);

        this.client = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                Authorization: `Bearer ${jwt}`,
                username: username
            },
            onConnect: () => {
                this.connected = true;
                this.connecting = false;
                console.log('✅ WebSocket Connected for:', username);

                // Subscripție pentru notificări de supraconsum
                this.client.subscribe('/user/queue/notifications', (msg) => {
                    const data = JSON.parse(msg.body);
                    this.notificationCallbacks.forEach(cb => cb(data));
                });

                // Subscripție pentru răspunsurile de la Chatbot AI sau Admin
                // Controller-ul folosește convertAndSendToUser către /queue/chat
                this.client.subscribe('/user/queue/chat', (msg) => {
                    const data = JSON.parse(msg.body);
                    console.log("📩 Mesaj primit pe chat:", data);
                    this.chatCallbacks.forEach(cb => cb(data));
                });

                // Subscripție pentru mesaje de suport (folosită de admini)
                this.client.subscribe('/topic/admin.support', (msg) => {
                    const data = JSON.parse(msg.body);
                    this.adminSupportCallbacks.forEach(cb => cb(data));
                });
            },
            onStompError: (frame) => {
                this.connecting = false;
                console.error('STOMP error', frame);
            },
            onDisconnect: () => {
                this.connected = false;
                this.connecting = false;
                console.log('❌ WebSocket Disconnected');
            }
        });
        this.client.activate();
    }

    // Metoda apelată pentru mesajele către AI
    sendChat(username, message) {
        if (!this.connected) {
            console.error("WebSocket not connected. Cannot send AI chat.");
            return;
        }
        this.client.publish({
            destination: '/app/chat.send',
            body: JSON.stringify({
                username: username,
                message: message,
                adminRequest: false
            })
        });
    }

    // Metoda apelată pentru mesajele către un Admin uman
    sendAdminChat(username, message) {
        if (!this.connected) {
            console.error("WebSocket not connected. Cannot send Admin chat.");
            return;
        }
        this.client.publish({
            destination: '/app/chat.send',
            body: JSON.stringify({
                username: username,
                message: message,
                adminRequest: true
            })
        });
    }

    // Metoda folosită de Admin pentru a răspunde unui client
    sendAdminReply(clientUsername, replyText) {
        if (!this.connected) return;
        this.client.publish({
            destination: '/app/chat.admin.reply',
            body: JSON.stringify({
                sender: clientUsername,
                message: replyText
            })
        });
    }

    disconnect() {
        if (this.client) {
            this.client.deactivate();
            this.client = null;
        }
        this.connected = false;
        this.connecting = false;
    }
}

export default new WebSocketClient();