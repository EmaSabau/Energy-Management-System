# Sistem de Management al Energiei - Arhitectură Microservicii

## 1. Prezentare Generală
Sistemul de Management al Energiei este o aplicație distribuită bazată pe microservicii, concepută pentru monitorizarea consumului de energie în timp real. Permite administratorilor să gestioneze utilizatorii și dispozitivele inteligente, oferind clienților monitorizare live, grafice de consum și asistență prin chat generat de Inteligență Artificială.

## 2. Arhitectura Sistemului

### Componente Principale:
- **Frontend**: Aplicație React (ikone Lucide, Tailwind CSS) pentru vizualizarea datelor și notificări.
- **Authorization Service**: Gestionează autentificarea și securitatea prin token-uri JWT.
- **User Service**: Managementul profilelor de utilizator.
- **Device Service**: Gestiunea contoarelor inteligente și asocierea lor cu utilizatorii.
- **Monitoring Service**: Procesează datele de la senzori și detectează depășirea limitei de consum orar.
- **WebSocket Service**: Punte de comunicare în timp real pentru alerte și chat.
- **Customer Support Service**: Asistent inteligent integrat cu **Google Gemini 2.0 Flash**.
- **Simulator**: Script Python care generează automat citiri de senzori.
- **RabbitMQ**: Broker de mesaje pentru comunicarea asincronă între componente.
- **Traefik**: API Gateway și Reverse Proxy pentru rutarea cererilor.

### Tehnologii Utilizate:
- **Backend**: Java 21, Spring Boot 3.4.1, Spring Security, JPA/Hibernate, RabbitMQ.
- **AI**: Google Gemini API (Model: gemini-2.0-flash).
- **Frontend**: React 18, WebSocket (STOMP/SockJS), Chart.js.
- **Baze de date**: PostgreSQL 15 (Instanțe separate pentru izolare).
- **Infrastructură**: Docker & Docker Compose, Traefik v3.0.

## 3. Configurare și Instalare

### Cerințe Minime:
- Docker și Docker Compose instalate.
- O cheie API validă pentru Google Gemini.

### Pași pentru Rulare:
1. Creează un fișier `.env` în directorul rădăcină:
   ```env
   POSTGRES_USER=utilizatorul_tau
   POSTGRES_PASSWORD=parola_ta
   GEMINI_API_KEY=cheia_ta_gemini_aici

Pornește serviciile folosind Docker Compose:
docker-compose up --build -d

Accesează serviciile:
Interfața Web: http://localhost (Port 80 prin Traefik)
Panou Control RabbitMQ: http://localhost:15672 (guest/guest)
Dashboard Traefik: http://localhost:8090

4. Workflow Mesagerie (RabbitMQ)
Sistemul utilizează cozi pentru a asigura funcționarea asincronă:
device_data_queue: Simulator -> Monitoring (Trimite măsurătorile brute).
overconsumption_alerts_queue: Monitoring -> WebSocket (Semnalează depășirea limitei).
chat_request_queue: WebSocket -> Customer Support (Întrebările utilizatorilor).
chat_response_queue: Customer Support -> WebSocket (Răspunsuri AI/Reguli).

5. Funcționalități Cheie
Notificări în Timp Real
Când consumul cumulat al unui dispozitiv depășește limita setată:
Monitoring Service identifică breșa.
Un eveniment este trimis prin RabbitMQ.
WebSocket Service notifică utilizatorul specific.
Frontend-ul afișează o alertă vizuală în partea stângă a ecranului.
Asistent Suport AI (Gemini)
Chatbot-ul integrat oferă suport în limba română:
Contextual: Știe să ofere informații despre sistemul de energie.
Avansat: Folosește Gemini 2.0 Flash pentru a răspunde la întrebări complexe dacă regulile locale nu se potrivesc.

6. Testare Manuală (Supraconsum)
Pentru a forța o alertă de supraconsum din Command Prompt (Windows):
docker exec -it rabbitmq rabbitmqadmin publish exchange=amq.default routing_key=device_data_queue payload="{\"timestamp\": \"2026-01-15T10:00:00\", \"device_id\": \"ID_UUID_REAL\", \"measurement_value\": 700.0}"