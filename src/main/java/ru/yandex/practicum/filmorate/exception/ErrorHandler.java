package ru.yandex.practicum.filmorate.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationException(ValidationException e) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 400,
                "error", e.getMessage()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NotFoundException e) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 404,
                "error", e.getMessage()
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Map<String, Object> handleRSE(ResponseStatusException e, HttpServletResponse response) {

        response.setStatus(e.getStatusCode().value());

        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", e.getStatusCode().value(),
                "error", e.getReason()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGeneric(Exception e) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 500,
                "error", "Произошла неожиданная ошибка. Пожалуйста, проверьте корректность запроса или повторите попытку позже."
        );
    }
}