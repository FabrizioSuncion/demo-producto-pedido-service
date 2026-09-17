package com.electrostore.pedidos.dto;

public class AuthResponseDTO {
    private String token;
    private String type = "Bearer";

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public String getType() { return type; }
}