package ru.yandex.practicum.filmorate.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private final int status;
    private final String message;
}