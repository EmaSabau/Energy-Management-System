package com.example.demo.dto;

import java.util.List;

public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private List<String> roles;

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public static class AuthResponseBuilder {
        private String token;
        private String username;
        private String email;
        private List<String> roles;

        public AuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuthResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AuthResponseBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public AuthResponse build() {
            AuthResponse response = new AuthResponse();
            response.setToken(this.token);
            response.setUsername(this.username);
            response.setEmail(this.email);
            response.setRoles(this.roles);
            return response;
        }
    }
}