package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private int id;

    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелов")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не указана")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;

    public String getName() {
        return (name == null || name.isBlank()) ? login : name;
    }
}