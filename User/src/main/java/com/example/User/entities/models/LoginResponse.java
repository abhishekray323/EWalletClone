package com.example.User.entities.models;

public record LoginResponse(String token, String tokenType, long expiresIn) {
}
