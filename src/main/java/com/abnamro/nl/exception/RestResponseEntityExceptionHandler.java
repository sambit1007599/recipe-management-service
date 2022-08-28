package com.abnamro.nl.exception;


import com.abnamro.nl.config.ErrorConstants;
import com.abnamro.nl.models.Error;
import com.abnamro.nl.models.ErrorAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final ErrorConstants errorConstants;


    @ExceptionHandler(NoRecipeFoundException.class)
    public ResponseEntity<Error> handleNoRecipeFoundException(NoRecipeFoundException ex) {

        Error error = getErrorResponseEntity(errorConstants.getNoRecipeFoundException(), HttpStatus.BAD_REQUEST, ex);

        log.error("{} :: {}", errorConstants.getNoRecipeFoundException().getCode(),
                errorConstants.getNoRecipeFoundException().getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private Error getErrorResponseEntity(ErrorAttributes errorAttributes, HttpStatus httpStatus, Exception ex) {

        Error error = new Error();
        error.setErrors(
                List.of(ErrorAttributes.builder().code(errorAttributes.getCode()).message(errorAttributes.getMessage())
                        .status(httpStatus.value()).params(List.of(ex.getMessage())).build()));

        return error;
    }

}
