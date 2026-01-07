package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

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
        userStorage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userStorage.update(user);
    }

    public void addFriend(long id, long friendId) {
        getById(id);
        getById(friendId);
        friendStorage.addFriend(id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        getById(id);
        getById(friendId);
        friendStorage.deleteFriend(id, friendId);
    }

    public List<User> getFriends(long id) {
        getById(id);
        return friendStorage.findFriendIds(id).stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long otherId) {
        getById(id);
        getById(otherId);

        var first = friendStorage.findFriendIds(id);
        var second = friendStorage.findFriendIds(otherId);

        first.retainAll(second);

        return first.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }
}