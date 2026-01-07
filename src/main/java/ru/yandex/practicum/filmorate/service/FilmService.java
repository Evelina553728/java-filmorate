package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate CINEMA_BIRTH = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserService userService;

    private final LikeStorage likeStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public java.util.List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getById(long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public Film create(Film film) {
        validateFilm(film);
        validateMpaAndGenresExist(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);

        filmStorage.findById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        validateMpaAndGenresExist(film);
        return filmStorage.update(film);
    }

    public void addLike(long filmId, long userId) {
        getById(filmId);
        userService.getById(userId);
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        getById(filmId);
        userService.getById(userId);
        likeStorage.removeLike(filmId, userId);
    }

    public java.util.List<Film> getPopular(int count) {
        if (count <= 0) {
            throw new ValidationException("count должен быть положительным");
        }
        return filmStorage.findPopular(count);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(CINEMA_BIRTH)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }

    private void validateMpaAndGenresExist(Film film) {
        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            mpaStorage.findById(mpaId)
                    .orElseThrow(() -> new NotFoundException("Рейтинг MPA не найден"));
        }

        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .filter(Objects::nonNull)
                    .forEach(g -> genreStorage.findById(g.getId())
                            .orElseThrow(() -> new NotFoundException("Жанр не найден")));
        }
    }
}