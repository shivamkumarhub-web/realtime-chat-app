package com.shivam.chat.dto;

import java.util.Set;

public class JwtResponse {
    private String token;
    private String type;
    private String username;
    private Set<String> roles;

    public JwtResponse(String token, String type, String username, Set<String> roles) {
        this.token = token; this.type = type; this.username = username; this.roles = roles;
    }
    public String getToken() { return token; }
    public String getType() { return type; }
    public String getUsername() { return username; }
    public Set<String> getRoles() { return roles; }
}