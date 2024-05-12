package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse(
                404,
                "Not Found",
                e.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistException(final AlreadyExistsException e) {
        return new ErrorResponse(
                409,
                "Conflict",
                e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnavailableException(final BadRequestException e) {
        return new ErrorResponse(
                400,
                "Bad Request",
                e.getMessage());
    }

    @ExceptionHandler(UnknownStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownStateException(final UnknownStateException e) {
        return new ErrorResponse(
                400,
                "Unknown state: " + e.getMessage(),
                "State must be: ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED");
    }
}
