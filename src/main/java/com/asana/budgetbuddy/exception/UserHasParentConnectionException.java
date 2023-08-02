package com.asana.budgetbuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserHasParentConnectionException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserHasParentConnectionException() { super("User already has a parent connection"); }
}
