package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();
    private Long nextId = 1L;

    public User create(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
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
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}