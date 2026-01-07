package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        getById(user.getId());
        return userStorage.update(user);
    }

    public void addFriend(long userId, long friendId) {
        getById(userId);
        getById(friendId);
        friendStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        getById(userId);
        getById(friendId);
        friendStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(long userId) {
        getById(userId);
        return friendStorage.findFriendIds(userId).stream()
                .map(this::getById)
                .toList();
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        getById(userId);
        getById(otherId);
        return friendStorage.findCommonFriendIds(userId, otherId).stream()
                .map(this::getById)
                .toList();
    }
}