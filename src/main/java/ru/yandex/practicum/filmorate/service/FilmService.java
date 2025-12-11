package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private static final LocalDate CINEMA_BIRTH = LocalDate.of(1895, 12, 28);

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film getById(long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public Film create(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public void addLike(long filmId, long userId) {
        Film film = getById(filmId);
        film.getLikes().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = getById(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTH)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}