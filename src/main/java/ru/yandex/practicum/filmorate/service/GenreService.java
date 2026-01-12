package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> findAll() {
        log.info("Получен запрос на получение всех жанров");
        return genreStorage.findAll();
    }

    public Genre getById(int id) {
        log.info("Получен запрос на получение жанра с id={}", id);

        return genreStorage.findById(id)
                .orElseThrow(() -> {
                    log.warn("Жанр с id={} не найден", id);
                    return new NotFoundException("Жанр не найден");
                });
    }
}