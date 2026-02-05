# Energy Management System 

## 1. Descrierea Proiectului
Acest sistem reprezintă o platformă completă pentru monitorizarea și gestionarea consumului de energie al dispozitivelor inteligente dintr-o locuință. Proiectul evoluează de la o structură de microservicii de bază (CRUD) la un sistem distribuit complex care utilizează comunicare asincronă, procesare de date în timp real și inteligență artificială.

## 2. Arhitectura Microserviciilor
Sistemul este compus din următoarele entități interconectate:

**Frontend (React):** Interfață de vizualizare cu grafice de consum orar, panouri de administrare și widget de chat AI.

**Authorization Service:** Gestionează securitatea aplicației, autentificarea utilizatorilor și generarea token-urilor JWT.

**User Management Service:** Administrează conturile utilizatorilor (Administrator și Client) și sincronizează datele între servicii.

**Device Management Service:** Gestionează inventarul de dispozitive inteligente și limitele de consum ale acestora.

**Monitoring Service:** Procesează măsurătorile de energie, agregă consumul orar și detectează automat depășirile de prag.

**WebSocket Service:** Releu pentru comunicații instantanee, trimițând alerte de supraconsum și mesaje de chat direct în browserul utilizatorului.

**Customer Support Service:** Modul inteligent care utilizează Google Gemini 2.0 Flash pentru a oferi suport tehnic și sfaturi energetice în limba română.

**Device Data Simulator:** Aplicație Python care emulează comportamentul senzorilor fizici, transmițând date la intervale de 10 minute.

## 3. Funcționalități Principale pe Etape
**Gestiune și Acces**
Autentificare: Role-based access control (Admin vs Client).
<img width="2541" height="1396" alt="image" src="https://github.com/user-attachments/assets/c24bd49d-0faa-448b-9ea1-3e0bcc7a648b" />

CRUD Utilizatori & Dispozitive: Gestiunea completă a bazei de date de către administratori.
<img width="2518" height="1413" alt="Screenshot 2026-01-22 124648" src="https://github.com/user-attachments/assets/a929eb12-2de4-426b-bf4d-6c2514338404" />
<img width="2557" height="1422" alt="Screenshot 2026-01-22 124700" src="https://github.com/user-attachments/assets/20922149-66d5-489a-842b-865d5ba63a04" />

Asociere: Maparea dispozitivelor către utilizatori specifici.

**Comunicare Asincronă și Monitorizare**
Messaging: Utilizarea RabbitMQ pentru a decupla simulatorul de sistemul de monitorizare.

Procesare Orară: Agregarea automată a valorilor primite de la senzori pe parcursul fiecărei ore.

Vizualizare: Grafice interactive realizate cu Recharts care afișează consumul is<img width="2490" height="1411" alt="Screenshot 2026-01-22 124816" src="https://github.com/user-attachments/assets/433edfcf-b964-4445-a235-e99521434814" />
toric.

**Real-Time & AI Support**
Notificări Live: Alerte instantanee transmise prin WebSockets atunci când consumul orar depășește pragul configurat.

Chatbot AI: Asistent virtual capabil să răspundă la întrebări despre sistem, folosind un Prompt de Sistem optimizat pentru suport clienți.
<img width="2556" height="1423" alt="Screenshot 2026-01-22 124849" src="https://github.com/user-attachments/assets/022db0ab-97e6-4a42-a3ea-0d3bfe427abb" />

Custommer Suport: Clientul poate să aleagă optțiunea de a vorbi cu un administrator.
<img width="2519" height="1414" alt="Screenshot 2026-01-22 124933" src="https://github.com/user-attachments/assets/1033fba7-3d50-4250-8c57-5c4ac70965c2" />


<img width="2509" height="1416" alt="Screenshot 2026-01-22 124943" src="https://github.com/user-attachments/assets/e637d7e3-507e-418a-bf23-af58b538b11f" />

## 4. Tehnologii și Infrastructură
Backend: Java 21, Spring Boot 3.4.1, Spring Security, JPA, RabbitMQ.

AI Integration: Google Gemini API (model gemini-2.0-flash).

Frontend: React, STOMP.js (pentru WebSockets), Recharts.

Mesagerie: RabbitMQ (cozi pentru date senzori, alerte și chat).

Infrastructură: Docker, Docker Compose și Traefik ca Reverse Proxy/Gateway.

## 5. Ghid de Instalare
Configurare Mediu
Creați un fișier .env în rădăcina proiectului cu următoarele chei:

Fragment de cod
<pre>
POSTGRES_USER=guest
POSTGRES_PASSWORD=guest
GEMINI_API_KEY=cheia_ta_gemini_aici
</pre>
Lansare Aplicație
Construiți și lansați containerele:

<pre>
docker-compose up --build -d
</pre>
Accesați interfața web la: http://localhost.

Monitorizați mesajele în RabbitMQ Management: http://localhost:15672 (guest/guest).

Rulare Simulator
Simulatorul Python poate fi rulat manual pentru a genera date instantanee:

<pre>
python simulator.py
</pre>
Acesta va trimite datele istorice de la 00:00 (Backfill) și va continua să trimită măsurători în timp real la fiecare 10 minute.
