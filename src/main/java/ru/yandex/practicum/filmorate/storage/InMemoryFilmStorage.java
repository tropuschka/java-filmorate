package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> filmList = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return filmList.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        filmList.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        filmList.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return Optional.ofNullable(filmList.get(id));
    }

    private Long getNextId() {
        long maxId = filmList.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
