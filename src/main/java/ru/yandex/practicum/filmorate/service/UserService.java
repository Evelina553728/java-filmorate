package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    public User create(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Пользователь с ID=" + user.getId() + " не найден"
            );
        }
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
