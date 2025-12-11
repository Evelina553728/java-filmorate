package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID=" + id + " не найден"));
    }

    public User create(User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return userStorage.update(user);
    }

    public void addFriend(long id, long friendId) {
        if (id == friendId) {
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }

        User user = getById(id);
        User friend = getById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(long id, long friendId) {
        User user = getById(id);
        User friend = getById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getFriends(long id) {
        User user = getById(id);
        if (user.getFriends() == null || user.getFriends().isEmpty()) {
            return List.of();
        }

        return user.getFriends().stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long otherId) {
        User u1 = getById(id);
        User u2 = getById(otherId);

        Set<Long> commonIds = new HashSet<>(u1.getFriends());
        commonIds.retainAll(u2.getFriends());

        return commonIds.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    private void validateUser(User user) {
        if (user.getLogin() != null && user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(java.time.LocalDate.now())) {
            throw new ValidationException("Дата рождения должна быть в прошлом");
        }
    }
}