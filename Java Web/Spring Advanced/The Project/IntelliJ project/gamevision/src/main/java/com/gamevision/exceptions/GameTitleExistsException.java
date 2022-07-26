package com.gamevision.exceptions;

public class GameTitleExistsException extends RuntimeException { //NonUniqueResultException
  private static final String MESSAGE = "A game with that title already exists.";
    public GameTitleExistsException() {
        super(MESSAGE);
    }

    @Override
    public String getMessage(){return MESSAGE;}
}
