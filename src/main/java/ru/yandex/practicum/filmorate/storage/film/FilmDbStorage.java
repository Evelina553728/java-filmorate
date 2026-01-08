package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Film> FILM_MAPPER = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        int mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("mpa_name");
        if (!rs.wasNull()) {
            film.setMpa(new Mpa(mpaId, mpaName));
        }

        film.setLikes(new java.util.HashSet<>());
        film.setGenres(new LinkedHashSet<>());
        return film;
    };

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(
                "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                        "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id ORDER BY f.id",
                FILM_MAPPER
        );

        for (Film film : films) {
            fillGenres(film);
            fillLikes(film);
        }
        return films;
    }

    @Override
    public Optional<Film> findById(long id) {
        List<Film> films = jdbcTemplate.query(
                "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                        "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?",
                FILM_MAPPER,
                id
        );

        Optional<Film> filmOpt = films.stream().findFirst();
        filmOpt.ifPresent(f -> {
            fillGenres(f);
            fillLikes(f);
        });
        return filmOpt;
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Integer mpaId = (film.getMpa() == null) ? null : film.getMpa().getId();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            if (mpaId == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, mpaId);
            }
            return ps;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();
        film.setId(id);

        updateGenres(id, film.getGenres());
        fillGenres(film);
        fillLikes(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        Integer mpaId = (film.getMpa() == null) ? null : film.getMpa().getId();

        jdbcTemplate.update(
                "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                mpaId,
                film.getId()
        );

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        updateGenres(film.getId(), film.getGenres());

        fillGenres(film);
        fillLikes(film);

        if (film.getMpa() != null) {
            String name = jdbcTemplate.queryForObject(
                    "SELECT name FROM mpa WHERE id = ?",
                    String.class,
                    film.getMpa().getId()
            );
            film.getMpa().setName(name);
        }
        return film;
    }

    @Override
    public List<Film> findPopular(int count) {
        List<Film> films = jdbcTemplate.query(
                "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                        "FROM films f " +
                        "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                        "LEFT JOIN likes l ON f.id = l.film_id " +
                        "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name " +
                        "ORDER BY COUNT(l.user_id) DESC, f.id ASC " +
                        "LIMIT ?",
                FILM_MAPPER,
                count
        );

        for (Film film : films) {
            fillGenres(film);
            fillLikes(film);
        }
        return films;
    }

    private void updateGenres(long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        for (Genre g : genres) {
            jdbcTemplate.update(
                    "MERGE INTO film_genres (film_id, genre_id) KEY(film_id, genre_id) VALUES (?, ?)",
                    filmId,
                    g.getId()
            );
        }
    }

    private void fillGenres(Film film) {
        List<Genre> genres = jdbcTemplate.query(
                "SELECT g.id, g.name FROM genres g " +
                        "JOIN film_genres fg ON fg.genre_id = g.id " +
                        "WHERE fg.film_id = ? ORDER BY g.id",
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")),
                film.getId()
        );
        film.setGenres(new LinkedHashSet<>(genres));
    }

    private void fillLikes(Film film) {
        List<Long> userIds = jdbcTemplate.query(
                "SELECT user_id FROM likes WHERE film_id = ?",
                (rs, rowNum) -> rs.getLong("user_id"),
                film.getId()
        );
        film.setLikes(new java.util.HashSet<>(userIds));
    }
}