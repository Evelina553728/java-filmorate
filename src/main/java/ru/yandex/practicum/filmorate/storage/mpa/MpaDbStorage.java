package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Mpa> MPA_MAPPER = (rs, rowNum) ->
            new Mpa(rs.getInt("id"), rs.getString("name"));

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query("SELECT id, name FROM mpa ORDER BY id", MPA_MAPPER);
    }

    @Override
    public Optional<Mpa> findById(int id) {
        List<Mpa> result = jdbcTemplate.query(
                "SELECT id, name FROM mpa WHERE id = ?",
                MPA_MAPPER,
                id
        );
        return result.stream().findFirst();
    }
}