package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public List<User> findAll() {
        log.info("Запрос всех пользователей");
        return userStorage.findAll();
    }

    public User getById(long id) {
        log.info("Запрос пользователя по id={}", id);
        return userStorage.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id={} не найден", id);
                    return new NotFoundException("Пользователь не найден");
                });
    }

    public User create(User user) {
        log.info("Создание пользователя: email='{}', login='{}'",
                user != null ? user.getEmail() : null,
                user != null ? user.getLogin() : null
        );
        User created = userStorage.create(user);
        log.info("Пользователь создан: id={}", created.getId());
        return created;
    }

    public User update(User user) {
        log.info("Обновление пользователя: id={}", user != null ? user.getId() : null);
        getById(user.getId());
        User updated = userStorage.update(user);
        log.info("Пользователь обновлён: id={}", updated.getId());
        return updated;
    }

    public void addFriend(long userId, long friendId) {
        log.info("Добавление друга: userId={}, friendId={}", userId, friendId);

        getById(userId);
        getById(friendId);

        friendStorage.addFriend(userId, friendId);
        log.info("Друг добавлен: userId={}, friendId={}", userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        log.info("Удаление друга: userId={}, friendId={}", userId, friendId);

        getById(userId);
        getById(friendId);

        friendStorage.deleteFriend(userId, friendId);
        log.info("Удаление друга выполнено: userId={}, friendId={}", userId, friendId);
    }

    public List<User> getFriends(long userId) {
        log.info("Запрос списка друзей: userId={}", userId);

        getById(userId);

        List<Long> friendIds = friendStorage.findFriendIds(userId);
        List<User> friends = friendIds.stream()
                .map(this::getById)
                .toList();

        log.info("Список друзей сформирован: userId={}, count={}", userId, friends.size());
        return friends;
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        log.info("Запрос общих друзей: userId={}, otherId={}", userId, otherId);

        getById(userId);
        getById(otherId);

        List<Long> ids = friendStorage.findCommonFriendIds(userId, otherId);
        List<User> common = ids.stream()
                .map(this::getById)
                .toList();

        log.info("Общие друзья сформированы: userId={}, otherId={}, count={}", userId, otherId, common.size());
        return common;
    }
}