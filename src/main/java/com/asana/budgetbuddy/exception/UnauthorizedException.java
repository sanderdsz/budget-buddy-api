package com.asana.budgetbuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UnauthorizedException() {
        super("Unauthorized access");
    }
}
