package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> findAll() {
        log.info("Получен запрос на получение всех рейтингов MPA");
        return mpaStorage.findAll();
    }

    public Mpa getById(int id) {
        log.info("Получен запрос на получение рейтинга MPA с id={}", id);

        return mpaStorage.findById(id)
                .orElseThrow(() -> {
                    log.warn("Рейтинг MPA с id={} не найден", id);
                    return new NotFoundException("Рейтинг не найден");
                });
    }
}