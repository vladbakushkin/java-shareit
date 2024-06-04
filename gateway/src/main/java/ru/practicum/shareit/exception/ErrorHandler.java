package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return new ErrorResponse(
                400,
                "Bad Request",
                "Неправильно составлен запрос. " +
                        "Поле: \"" + Objects.requireNonNull(e.getFieldError()).getField() + "\" " +
                        "Причина: \"" + Objects.requireNonNull(e.getFieldError()).getDefaultMessage() + "\"");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException e) {
        return new ErrorResponse(
                400,
                "Bad Request",
                "Неправильно составлен запрос. " +
                        "Поле: \"" + e.getConstraintViolations().iterator().next().getPropertyPath() + "\" " +
                        "Причина: \"" + e.getConstraintViolations().iterator().next().getMessage() + "\"");
    }
}
