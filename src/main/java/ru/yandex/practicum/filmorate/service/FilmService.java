package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate CINEMA_BIRTH = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeStorage likeStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public List<Film> findAll() {
        log.info("Запрос на получение списка всех фильмов");
        return filmStorage.findAll();
    }

    public Film getById(long id) {
        log.info("Запрос на получение фильма по id={}", id);
        return filmStorage.findById(id)
                .orElseThrow(() -> {
                    log.warn("Фильм с id={} не найден", id);
                    return new NotFoundException("Фильм не найден");
                });
    }

    public Film create(Film film) {
        if (film == null) {
            log.warn("Некорректный запрос на создание фильма: film=null");
            throw new ValidationException("Фильм не должен быть null");
        }
        log.info("Запрос на создание фильма: name='{}'", film.getName());

        validateFilm(film);
        validateMpaAndGenresExist(film);

        Film created = filmStorage.create(film);
        log.info("Фильм создан: id={}", created.getId());
        return created;
    }

    public Film update(Film film) {
        if (film == null) {
            log.warn("Некорректный запрос на обновление фильма: film=null");
            throw new ValidationException("Фильм не должен быть null");
        }
        log.info("Запрос на обновление фильма: id={}", film.getId());

        validateFilm(film);

        filmStorage.findById(film.getId())
                .orElseThrow(() -> {
                    log.warn("Нельзя обновить: фильм с id={} не найден", film.getId());
                    return new NotFoundException("Фильм не найден");
                });

        validateMpaAndGenresExist(film);

        Film updated = filmStorage.update(film);
        log.info("Фильм обновлён: id={}", updated.getId());
        return updated;
    }

    public void addLike(long filmId, long userId) {
        log.info("Запрос на добавление лайка: filmId={}, userId={}", filmId, userId);

        getById(filmId);
        userService.getById(userId);

        likeStorage.addLike(filmId, userId);

        log.info("Лайк добавлен: filmId={}, userId={}", filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        log.info("Запрос на удаление лайка: filmId={}, userId={}", filmId, userId);

        getById(filmId);
        userService.getById(userId);

        likeStorage.removeLike(filmId, userId);

        log.info("Лайк удалён: filmId={}, userId={}", filmId, userId);
    }

    public List<Film> getPopular(int count) {
        log.info("Запрос на получение популярных фильмов: count={}", count);

        List<Film> result = filmStorage.findPopular(count);

        log.info("Популярные фильмы сформированы: returned={}", result.size());
        return result;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(CINEMA_BIRTH)) {
            log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Некорректная продолжительность: {}", film.getDuration());
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }

    private void validateMpaAndGenresExist(Film film) {
        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            mpaStorage.findById(mpaId)
                    .orElseThrow(() -> {
                        log.warn("MPA с id={} не найден", mpaId);
                        return new NotFoundException("Рейтинг MPA не найден");
                    });
        }

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> {
                if (genre == null) {
                    log.warn("Некорректный жанр: genre=null");
                    throw new ValidationException("Некорректный жанр");
                }
                int genreId = genre.getId();
                genreStorage.findById(genreId)
                        .orElseThrow(() -> {
                            log.warn("Жанр с id={} не найден", genreId);
                            return new NotFoundException("Жанр не найден");
                        });
            });
        }
    }
}