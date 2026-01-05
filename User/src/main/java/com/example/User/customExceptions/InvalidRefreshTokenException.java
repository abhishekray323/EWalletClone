package com.example.User.customExceptions;

public class InvalidRefreshTokenException extends  RuntimeException{
    public InvalidRefreshTokenException() {
        super("Invalid refresh token");
    }
}
