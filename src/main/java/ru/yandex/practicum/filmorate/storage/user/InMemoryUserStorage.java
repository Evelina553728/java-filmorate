package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(idCounter++);
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        users.get(userId).getFriends().add(friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        users.get(userId).getFriends().remove(friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        return users.get(userId).getFriends().stream()
                .map(users::get)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        Set<Long> first = users.get(userId).getFriends();
        Set<Long> second = users.get(otherId).getFriends();

        return first.stream()
                .filter(second::contains)
                .map(users::get)
                .toList();
    }
}