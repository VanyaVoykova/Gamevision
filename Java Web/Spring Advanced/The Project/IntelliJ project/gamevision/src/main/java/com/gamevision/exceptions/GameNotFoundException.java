package com.gamevision.exceptions;

public class GameNotFoundException extends RuntimeException {
private static final String MESSAGE = "Game not found.";

    public GameNotFoundException() {
        super(MESSAGE);
    }


}
