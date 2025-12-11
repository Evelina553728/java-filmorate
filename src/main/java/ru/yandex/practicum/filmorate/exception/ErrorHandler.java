package ru.yandex.practicum.filmorate.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    public Map<String, Object> handleValidation(ValidationException e) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        return buildError(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, Object> handleConstraintViolation(ConstraintViolationException e) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public Map<String, Object> handleNotFound(NotFoundException e) {
        return buildError(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public Map<String, Object> handleGeneric(Throwable e) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Произошла неожиданная ошибка. Пожалуйста, проверьте корректность запроса или повторите попытку позже."
        );
    }

    private Map<String, Object> buildError(HttpStatus status, String message) {
        return Map.of(
                "status", status.value(),
                "timestamp", LocalDateTime.now(),
                "error", message
        );
    }
}