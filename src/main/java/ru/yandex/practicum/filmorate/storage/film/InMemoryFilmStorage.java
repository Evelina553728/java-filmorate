package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        Film film = findById(filmId).orElseThrow();
        film.getLikes().add(userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        Film film = findById(filmId).orElseThrow();
        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> findPopular(int count) {
        return findAll().stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getLikes().size(), a.getLikes().size());
                    if (cmp != 0) return cmp;
                    return Long.compare(a.getId(), b.getId()); // стабильность при равенстве
                })
                .limit(count)
                .toList();
    }
}