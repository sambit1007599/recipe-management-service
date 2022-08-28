package com.abnamro.nl.exception;

import lombok.Getter;

@Getter
public class NoRecipeFoundException extends RuntimeException {

    public NoRecipeFoundException(final String message) {
        super(message);
    }
}
