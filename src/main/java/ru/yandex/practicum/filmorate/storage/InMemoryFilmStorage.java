package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ExceptionService;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> filmList = new HashMap<>();
    private static final ExceptionService exceptionService = new ExceptionService();

    @Override
    public Collection<Film> findAll() {
        return filmList.values();
    }

    @Override
    public Film create(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            exceptionService.throwValidationException(new ConditionsNotMetException("Название должно быть указано"));
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            exceptionService.throwValidationException(new ConditionsNotMetException("Описание не должно быть длиннее 200 символов"));
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            exceptionService.throwValidationException(new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1885 года"));
        }
        if (film.getDuration() != null && !(film.getDuration() > 0)) {
            exceptionService.throwValidationException(new ConditionsNotMetException("Длительность фильма должна быть положительной"));
        }
        film.setId(getNextId());
        filmList.put(film.getId(), film);
        log.trace("Фильм {}, айди {}, добавлен", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null) {
            exceptionService.throwValidationException(new ConditionsNotMetException("Id должен быть указан"));
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
                exceptionService.throwValidationException(new ConditionsNotMetException("Описание не должно быть длиннее 200 символов"));
            }
            newFilm.setDescription(film.getDescription());
        } else {
            newFilm.setDescription(oldFilm.getDescription());
        }
        if (film.getDuration() != null) {
            if (!(film.getDuration() > 0)) {
                exceptionService.throwValidationException(new ConditionsNotMetException("Длительность фильма должна быть положительной"));
            }
            newFilm.setDuration(film.getDuration());
        } else {
            newFilm.setDuration(oldFilm.getDuration());
        }
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                exceptionService.throwValidationException(new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1885 года"));
            }
            newFilm.setReleaseDate(film.getReleaseDate());
        } else {
            newFilm.setReleaseDate(oldFilm.getReleaseDate());
        }

        filmList.put(film.getId(), newFilm);
        log.trace("Данные фильма {}, айди {}, обновлены", film.getName(), film.getId());
        return newFilm;
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
