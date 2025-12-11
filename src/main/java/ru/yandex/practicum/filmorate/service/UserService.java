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

    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User getById(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Пользователь с ID=" + id + " не найден");
        }
        return user;
    }

    public User create(User user) {
        user.setId(nextId++);
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        User existing = users.get(user.getId());
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Пользователь с ID=" + user.getId() + " не найден");
        }

        existing.setEmail(user.getEmail());
        existing.setLogin(user.getLogin());
        existing.setName(user.getName());
        existing.setBirthday(user.getBirthday());

        return existing;
    }

    public User addFriend(long id, long friendId) {
        User user = getById(id);
        User friend = getById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        return user;
    }

    public User deleteFriend(long id, long friendId) {
        User user = getById(id);
        User friend = getById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);

        return user;
    }

    public List<User> getFriends(long id) {
        User user = getById(id);

        List<User> list = new ArrayList<>();
        for (Long fid : user.getFriends()) {
            list.add(getById(fid));
        }
        return list;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        User u1 = getById(id);
        User u2 = getById(otherId);

        List<User> result = new ArrayList<>();

        for (Long friendId : u1.getFriends()) {
            if (u2.getFriends().contains(friendId)) {
                result.add(getById(friendId));
            }
        }
        return result;
    }
}