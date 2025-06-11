package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> filmList = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return filmList.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throwValidationException("Название должно быть указано");
        }
        if (film.getDescription().length() > 200) {
            throwValidationException("Описание не должно быть длиннее 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throwValidationException("Дата релиза не может быть раньше 28 декабря 1885 года");
        }
        if (!(film.getDuration() > 0)) {
            throwValidationException("Длительность фильма должна быть положительной");
        }
        film.setId(getNextId());
        filmList.put(film.getId(), film);
        log.trace("Фильм {}, айди {}, добавлен", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film.getId() == null) {
            throwValidationException("Id должен быть указан");
        }
        if (!(filmList.containsKey(film.getId()))) {
            throwNotFoundException("Фильм не найден");
        }
        Film oldFilm = filmList.get(film.getId());
        Film newFilm = new Film();
        newFilm.setId(film.getId());

        if (film.getName() != null) {
            newFilm.setName(film.getName());
        } else {
            newFilm.setName(oldFilm.getName());
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > 200) {
                throwValidationException("Описание не должно быть длиннее 200 символов");
            }
            newFilm.setDescription(film.getDescription());
        } else {
            newFilm.setDescription(oldFilm.getDescription());
        }
        if (film.getDuration() != null) {
            if (!(film.getDuration() > 0)) {
                throwValidationException("Длительность фильма должна быть положительной");
            }
            newFilm.setDuration(film.getDuration());
        } else {
            newFilm.setDuration(oldFilm.getDuration());
        }
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throwValidationException("Дата релиза не может быть раньше 28 декабря 1885 года");
            }
            newFilm.setReleaseDate(film.getReleaseDate());
        } else {
            newFilm.setReleaseDate(oldFilm.getReleaseDate());
        }

        filmList.remove(film.getId());
        filmList.put(film.getId(), newFilm);
        log.trace("Данные фильма {}, айди {}, обновлены", film.getName(), film.getId());
        return newFilm;
    }

    private Integer getNextId() {
        int maxId = filmList.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }

    private void throwValidationException(String message) {
        log.error(message);
        throw new ConditionsNotMetException(message);
    }

    private void throwNotFoundException(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }
}
