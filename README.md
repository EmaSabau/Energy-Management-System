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

CRUD Utilizatori & Dispozitive: Gestiunea completă a bazei de date de către administratori.

Asociere: Maparea dispozitivelor către utilizatori specifici.

**Comunicare Asincronă și Monitorizare**
Messaging: Utilizarea RabbitMQ pentru a decupla simulatorul de sistemul de monitorizare.

Procesare Orară: Agregarea automată a valorilor primite de la senzori pe parcursul fiecărei ore.

Vizualizare: Grafice interactive realizate cu Recharts care afișează consumul istoric.

**Real-Time & AI Support**
Notificări Live: Alerte instantanee transmise prin WebSockets atunci când consumul orar depășește pragul configurat.

Chatbot AI: Asistent virtual capabil să răspundă la întrebări despre sistem, folosind un Prompt de Sistem optimizat pentru suport clienți.

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