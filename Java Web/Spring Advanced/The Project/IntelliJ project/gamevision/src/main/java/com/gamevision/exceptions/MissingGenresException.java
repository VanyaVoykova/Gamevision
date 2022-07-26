package com.gamevision.exceptions;

public class MissingGenresException extends RuntimeException{
    public MissingGenresException(String message) {
        super(message);
    }
}
