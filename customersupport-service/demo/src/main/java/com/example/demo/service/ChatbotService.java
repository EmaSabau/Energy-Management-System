package com.example.demo.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ChatbotService {

    private final List<ChatRule> rules = new ArrayList<>();

    @PostConstruct
    public void initRules() {
        rules.add(new ChatRule(
                Pattern.compile(".*\\b(buna|salut|hey|salutari|bună ziua)\\b.*", Pattern.CASE_INSENSITIVE),
                "Bună! Sunt asistentul tău pentru gestionarea energiei. Cum te pot ajuta astăzi?"
        ));

        rules.add(new ChatRule(
                Pattern.compile(".*\\b(ajutor|meniu|optiuni|ce poti face|ce stii)\\b.*", Pattern.CASE_INSENSITIVE),
                "Te pot ajuta cu următoarele:\n" +
                        "• Gestionarea dispozitivelor (adăugare, ștergere, vizualizare)\n" +
                        "• Monitorizarea consumului de energie în timp real\n" +
                        "• Alerte de supraconsum\n" +
                        "• Gestionarea contului și setări\n" +
                        "• Informații generale despre sistem\n\n" +
                        "Întreabă-mă orice!"
        ));

        rules.add(new ChatRule(
                Pattern.compile(".*\\b(adauga|creeaza|nou)\\b.*\\b(dispozitiv|aparat)\\b.*", Pattern.CASE_INSENSITIVE),
                "Pentru a adăuga un dispozitiv nou:\n" +
                        "Contacteaza administratorul\n"
        ));

        rules.add(new ChatRule(
                Pattern.compile(".*\\b(supraconsum|alerta|notificare|depasire|avertisment)\\b.*", Pattern.CASE_INSENSITIVE),
                "Alertele de supraconsum apar când un dispozitiv depășește limita orară setată.\n\n" +
                        "Vei primi:\n" +
                        "• Notificări popup în timp real (în stânga ecranului)\n" +
                        "• Detalii despre dispozitivul vizat\n" +
                        "• Compararea consumului curent cu limita maximă\n\n"
        ));

        rules.add(new ChatRule(
                Pattern.compile(".*\\b(cont|parola|resetare|schimbare parola|uitat)\\b.*", Pattern.CASE_INSENSITIVE),
                "Gestionare cont:\n" +
                        " Actualizare Date: Contactează administratorul sistemului\n"


        ));

        rules.add(new ChatRule(
                Pattern.compile(".*\\b(simulator|date test|demo|exemplu|simulare)\\b.*", Pattern.CASE_INSENSITIVE),
                "Despre Simulator:\n" +
                        "Sistemul folosește un simulator de date pentru a genera modele de consum realiste:\n" +
                        "• Generare date la fiecare 10 minute\n" +
                        "• Simulează comportamentul aparatelor pe zi/noapte\n" +
                        "• Permite testarea alertelor fără senzori reali montați\n\n" +
                        "Este ideal pentru a înțelege cum funcționează monitorizarea!"
        ));

        rules.add(new ChatRule(
                Pattern.compile(".*\\b(admin|suport|contact|om|vorbeste cu cineva)\\b.*", Pattern.CASE_INSENSITIVE),
                "Ai nevoie de asistență personalizată?\n" +
                        "Ai un buton pentru a contacta adminul...\n\n" +
                        "Te rugăm să descrii problema în fereastra de chat și cineva îți va răspunde în cel mai scurt timp."
        ));

        rules.add(new ChatRule(
                Pattern.compile(".*\\b(status|functionare|picat|problema|eroare)\\b.*", Pattern.CASE_INSENSITIVE),
                "Verificare Status Sistem: Toate serviciile sunt operaționale.\n\n" +
                        "• Gestionare Dispozitive: 🟢 Online\n" +
                        "• Serviciu Monitorizare: 🟢 Online\n" +
                        "• Notificări WebSocket: 🟢 Online\n" +
                        "• Simulator Consum: 🟢 Activ\n\n" +
                        "Ultima verificare: Chiar acum."
        ));

        rules.add(new ChatRule(
                Pattern.compile(".*\\b(multumesc|mersi|thx|ty|multu)\\b.*", Pattern.CASE_INSENSITIVE),
                "Cu mult drag! Mă bucur că te-am putut ajuta. Mai există și altceva cu ce te pot asista?"
        ));

        rules.add(new ChatRule(
                Pattern.compile(".*\\b(pa|la revedere|inchide|iesire)\\b.*", Pattern.CASE_INSENSITIVE),
                "La revedere! Îți doresc o zi eficientă din punct de vedere energetic! ⚡"
        ));

        System.out.println(" Chatbot inițializat cu " + rules.size() + " reguli în limba română.");
    }

    public String processMessage(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Nu am înțeles mesajul. Poți reformula întrebarea?";
        }

        String trimmed = userMessage.trim();
        for (ChatRule rule : rules) {
            if (rule.matches(trimmed)) {
                return rule.getResponse();
            }
        }

        return "Îmi pare rău, nu am un răspuns pentru această întrebare. Încearcă să ceri ajutorul unui 'admin'.";
    }

    private static class ChatRule {
        private final Pattern pattern;
        private final String response;

        public ChatRule(Pattern pattern, String response) {
            this.pattern = pattern;
            this.response = response;
        }

        public boolean matches(String message) {
            return pattern.matcher(message).matches();
        }

        public Pattern getPattern() {
            return pattern;
        }

        public String getResponse() {
            return response;
        }
    }
}