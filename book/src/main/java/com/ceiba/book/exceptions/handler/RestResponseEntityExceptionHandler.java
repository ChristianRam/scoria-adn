package com.ceiba.book.exceptions.handler;

import com.ceiba.book.exceptions.AlreadyExistException;
import com.ceiba.book.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(
            NotFoundException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(),
                null, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AlreadyExistException.class)
    protected ResponseEntity<Object> handleAlreadyExistException(
            AlreadyExistException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(),
                null, HttpStatus.CONFLICT, request);
    }
}
