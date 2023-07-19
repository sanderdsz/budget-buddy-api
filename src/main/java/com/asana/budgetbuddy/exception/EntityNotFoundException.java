package com.asana.budgetbuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.NoSuchElementException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends NoSuchElementException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(String message) {
        super(message);
    }
}
