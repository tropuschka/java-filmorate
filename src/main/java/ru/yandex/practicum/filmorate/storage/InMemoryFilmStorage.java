package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ExceptionService;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> filmList = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return filmList.values();
    }

    @Override
    public Film create(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            ExceptionService.throwValidationException("Название должно быть указано");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            ExceptionService.throwValidationException("Описание не должно быть длиннее 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            ExceptionService.throwValidationException("Дата релиза не может быть раньше 28 декабря 1885 года");
        }
        if (film.getDuration() != null && !(film.getDuration() > 0)) {
            ExceptionService.throwValidationException("Длительность фильма должна быть положительной");
        }
        film.setId(getNextId());
        filmList.put(film.getId(), film);
        log.trace("Фильм {}, айди {}, добавлен", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null) {
            ExceptionService.throwValidationException("Id должен быть указан");
        }
        if (!filmList.containsKey(film.getId())) {
            ExceptionService.throwNotFoundException("Фильм не найден");
        }
        Film oldFilm = filmList.get(film.getId());
        Film newFilm = new Film();
        newFilm.setId(film.getId());

        if (film.getName() != null && !film.getName().isBlank()) {
            newFilm.setName(film.getName());
        } else {
            newFilm.setName(oldFilm.getName());
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > 200) {
                ExceptionService.throwValidationException("Описание не должно быть длиннее 200 символов");
            }
            newFilm.setDescription(film.getDescription());
        } else {
            newFilm.setDescription(oldFilm.getDescription());
        }
        if (film.getDuration() != null) {
            if (!(film.getDuration() > 0)) {
                ExceptionService.throwValidationException("Длительность фильма должна быть положительной");
            }
            newFilm.setDuration(film.getDuration());
        } else {
            newFilm.setDuration(oldFilm.getDuration());
        }
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                ExceptionService.throwValidationException("Дата релиза не может быть раньше 28 декабря 1885 года");
            }
            newFilm.setReleaseDate(film.getReleaseDate());
        } else {
            newFilm.setReleaseDate(oldFilm.getReleaseDate());
        }

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
}
