package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public FriendshipStatus addFriend(long userId, long friendId) {
        Integer reverseCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?",
                Integer.class,
                friendId,
                userId
        );

        if (reverseCount != null && reverseCount > 0) {
            jdbcTemplate.update(
                    "MERGE INTO friends (user_id, friend_id, status) KEY(user_id, friend_id) VALUES (?, ?, ?)",
                    userId, friendId, FriendshipStatus.CONFIRMED.name()
            );
            jdbcTemplate.update(
                    "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?",
                    FriendshipStatus.CONFIRMED.name(), friendId, userId
            );
            return FriendshipStatus.CONFIRMED;
        }

        jdbcTemplate.update(
                "MERGE INTO friends (user_id, friend_id, status) KEY(user_id, friend_id) VALUES (?, ?, ?)",
                userId, friendId, FriendshipStatus.UNCONFIRMED.name()
        );
        return FriendshipStatus.UNCONFIRMED;
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", userId, friendId);

        jdbcTemplate.update(
                "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ? AND status = ?",
                FriendshipStatus.UNCONFIRMED.name(), friendId, userId, FriendshipStatus.CONFIRMED.name()
        );
    }

    @Override
    public List<Long> findFriendIds(long userId) {
        return jdbcTemplate.query(
                "SELECT friend_id FROM friends WHERE user_id = ? ORDER BY friend_id",
                (rs, rowNum) -> rs.getLong("friend_id"),
                userId
        );
    }

    @Override
    public List<Long> findCommonFriendIds(long userId, long otherId) {
        return jdbcTemplate.query(
                "SELECT f1.friend_id " +
                        "FROM friends f1 " +
                        "JOIN friends f2 ON f1.friend_id = f2.friend_id " +
                        "WHERE f1.user_id = ? AND f2.user_id = ? " +
                        "ORDER BY f1.friend_id",
                (rs, rowNum) -> rs.getLong("friend_id"),
                userId,
                otherId
        );
    }
}