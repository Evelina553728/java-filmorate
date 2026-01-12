package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    };

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users ORDER BY id", USER_MAPPER);
    }

    @Override
    public Optional<User> findById(long id) {
        return jdbcTemplate.query(
                "SELECT * FROM users WHERE id = ?",
                USER_MAPPER,
                id
        ).stream().findFirst();
    }

    @Override
    public User create(User user) {
        jdbcTemplate.update(
                "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday()
        );
        Long id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM users", Long.class);
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update(
                "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()
        );
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        jdbcTemplate.update(
                "MERGE INTO friends (user_id, friend_id) KEY (user_id, friend_id) VALUES (?, ?)",
                userId, friendId
        );
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        jdbcTemplate.update(
                "DELETE FROM friends WHERE user_id = ? AND friend_id = ?",
                userId, friendId
        );
    }

    @Override
    public List<User> getFriends(long userId) {
        return jdbcTemplate.query(
                """
                SELECT u.* FROM users u
                JOIN friends f ON u.id = f.friend_id
                WHERE f.user_id = ?
                ORDER BY u.id
                """,
                USER_MAPPER,
                userId
        );
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        return jdbcTemplate.query(
                """
                SELECT u.* FROM users u
                JOIN friends f1 ON u.id = f1.friend_id
                JOIN friends f2 ON u.id = f2.friend_id
                WHERE f1.user_id = ? AND f2.user_id = ?
                ORDER BY u.id
                """,
                USER_MAPPER,
                userId, otherId
        );
    }
}