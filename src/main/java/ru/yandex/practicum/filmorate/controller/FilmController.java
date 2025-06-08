package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

@RestController
public class FilmController {
    HashMap<Integer, Film> filmList = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return filmList.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название должно быть указано");
        }
        film.setId(getNextId());
        filmList.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        Film oldFilm = filmList.get(film.getId());
        Film newFilm = null;
        newFilm.setId(film.getId());

        if (film.getName() != null) {
            newFilm.setName(film.getName());
        } else {
            newFilm.setName(oldFilm.getName());
        }
        if (film.getDescription() != null) {
            newFilm.setDescription(film.getDescription());
        } else {
            newFilm.setDescription(oldFilm.getDescription());
        }
        if (film.getDuration() != null) {
            newFilm.setDuration(film.getDuration());
        } else {
            newFilm.setDuration(oldFilm.getDuration());
        }
        if (film.getReleaseDate() != null) {
            newFilm.setReleaseDate(film.getReleaseDate());
        } else {
            newFilm.setReleaseDate(oldFilm.getReleaseDate());
        }

        filmList.remove(film.getId());
        filmList.put(film.getId(), newFilm);
        return newFilm;
    }

    private Integer getNextId() {
        int maxId = filmList.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
