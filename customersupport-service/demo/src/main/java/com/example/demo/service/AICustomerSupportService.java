package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AICustomerSupportService {

    @Value("${gemini.api.key:}")
    private String apiKey;
    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public String getAIResponse(String userMessage) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Suportul AI este momentan indisponibil. Te rugăm să contactezi un administrator pentru asistență.";
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String systemPrompt = buildSystemPrompt();
            String fullPrompt = systemPrompt + "\n\nÎntrebarea utilizatorului: " + userMessage;

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", fullPrompt)
                            ))
                    )
            );

            String url = GEMINI_API_URL + "?key=" + apiKey;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseGeminiResponse(response.getBody());
            }

            return "Îmi pare rău, nu am putut procesa solicitarea ta. Te rog să reformulezi întrebarea.";

        } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
            return "Am primit prea multe mesaje într-un timp scurt. Te rog să aștepți un minut înainte de a încerca din nou.";
        } catch (Exception e) {
            System.err.println(" Eroare la apelarea Gemini API: " + e.getMessage());
            return "Întâmpin dificultăți tehnice momentan. Te rog să încerci mai târziu.";
        }
    }

    private String buildSystemPrompt() {
        return "Ești un asistent de suport clienți amabil și bine informat pentru un Sistem de Management al Energiei. " +
                "Sistemul ajută utilizatorii să:\n" +
                "- Monitorizeze consumul de energie al dispozitivelor lor în timp real.\n" +
                "- Urmărească utilizarea orară a energiei prin grafice interactive.\n" +
                "- Primească alerte (notificări roșii în stânga ecranului) când dispozitivele depășesc limita maximă de consum.\n" +
                "- Gestioneze mai multe dispozitive inteligente (adăugare, editare, ștergere).\n" +
                "- Vizualizeze date istorice de consum.\n\n" +
                "Funcționalități cheie:\n" +
                "- Management Dispozitive: Administratorii pot seta limite maxime de consum (kW/h) pentru fiecare aparat.\n" +
                "- Monitorizare Real-time: Notificări instantanee prin WebSocket pentru supraconsum.\n" +
                "- Vizualizare Date: Grafice de consum orar cu selector de dată.\n" +
                "- Acces pe roluri: Administratorii pot gestiona toți utilizatorii și toate dispozitivele.\n\n" +
                "Instrucțiuni de răspuns:\n" +
                "- Răspunde întotdeauna în limba română.\n" +
                "- Oferă răspunsuri clare, concise și utile.\n" +
                "- Dacă nu știi un detaliu specific, sugerează contactarea unui administrator.\n";
    }

    private String parseGeminiResponse(Map<String, Object> responseBody) {
        try {
            List candidates = (List) responseBody.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map firstCandidate = (Map) candidates.get(0);
                Map content = (Map) firstCandidate.get("content");
                List parts = (List) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    Map firstPart = (Map) parts.get(0);
                    return (String) firstPart.get("text");
                }
            }
        } catch (Exception e) {
            System.err.println(" Eroare la parsarea răspunsului Gemini: " + e.getMessage());
        }

        return "Nu am înțeles solicitarea. Te rog să încerci din nou!";
    }

    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}