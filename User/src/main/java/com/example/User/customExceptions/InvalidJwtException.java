package com.example.User.customExceptions;

public class InvalidJwtException extends  RuntimeException{
    public InvalidJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
