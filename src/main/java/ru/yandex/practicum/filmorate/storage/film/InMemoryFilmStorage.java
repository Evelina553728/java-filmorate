package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long idSeq = 1;

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
        film.setId(idSeq++);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> findPopular(int count) {
        return findAll().stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getLikes().size(), a.getLikes().size());
                    return (cmp != 0) ? cmp : Long.compare(a.getId(), b.getId()); // стабильность
                })
                .limit(count)
                .toList();
    }
}