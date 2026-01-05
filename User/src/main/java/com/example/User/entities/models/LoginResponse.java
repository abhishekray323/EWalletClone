package com.example.User.entities.models;

public record LoginResponse(String token, String refreshToken, String tokenType, long expiresInMs) {
}
