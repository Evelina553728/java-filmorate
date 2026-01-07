package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Primary
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(long filmId, long userId) {
        jdbcTemplate.update(
                "MERGE INTO likes (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)",
                filmId, userId
        );
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update(
                "DELETE FROM likes WHERE film_id = ? AND user_id = ?",
                filmId, userId
        );
    }

    @Override
    public Set<Long> findLikesByFilmId(long filmId) {
        List<Long> ids = jdbcTemplate.query(
                "SELECT user_id FROM likes WHERE film_id = ? ORDER BY user_id",
                (rs, rowNum) -> rs.getLong("user_id"),
                filmId
        );
        return new HashSet<>(ids);
    }
}