package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AgeRatingStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final AgeRatingStorage ageRatingStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("AgeRatingDbStorage") AgeRatingStorage ageRatingStorage,
                       @Qualifier("GenreDbStorage") GenreStorage genreStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.ageRatingStorage = ageRatingStorage;
        this.genreStorage = genreStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название должно быть указано");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("Описание не должно быть длиннее 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1885 года");
        }
        if (film.getDuration() != null && !(film.getDuration() > 0)) {
            throw new ConditionsNotMetException("Длительность фильма должна быть положительной");
        }
        if (film.getMpa() == null) {
            throw new ConditionsNotMetException("Возрастной рейтинг должен быть указан");
        }
        if (ageRatingStorage.findAgeRatingById(film.getId()).isEmpty()) {
            throw new NotFoundException("Возрастная категория не найдена");
        }
        if (!film.getGenres().isEmpty()) {
            for (Genre genre:film.getGenres()) {
                genreStorage.findGenreById(genre.getId()).orElseThrow(() -> new NotFoundException("Жанр не найден"));
            }
        }
        Film createdFilm = filmStorage.create(film);
        log.trace("Фильм {}, айди {}, добавлен", createdFilm.getName(), createdFilm.getId());
        return createdFilm;
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        Film oldFilm = filmStorage.findFilmById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        Film newFilm = new Film();
        newFilm.setId(film.getId());

        if (film.getName() != null && !film.getName().isBlank()) {
            newFilm.setName(film.getName());
        } else {
            newFilm.setName(oldFilm.getName());
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > 200) {
                throw new ConditionsNotMetException("Описание не должно быть длиннее 200 символов");
            }
            newFilm.setDescription(film.getDescription());
        } else {
            newFilm.setDescription(oldFilm.getDescription());
        }
        if (film.getDuration() != null) {
            if (!(film.getDuration() > 0)) {
                throw new ConditionsNotMetException("Длительность фильма должна быть положительной");
            }
            newFilm.setDuration(film.getDuration());
        } else {
            newFilm.setDuration(oldFilm.getDuration());
        }
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1885 года");
            }
            newFilm.setReleaseDate(film.getReleaseDate());
        } else {
            newFilm.setReleaseDate(oldFilm.getReleaseDate());
        }
        if (film.getGenres() != null) {
            if (!film.getGenres().isEmpty()) {
                for (Genre genre:film.getGenres()) {
                    genreStorage.findGenreById(genre.getId()).orElseThrow(() -> new NotFoundException("Жанр не найден"));
                }
            }
            newFilm.setGenres(film.getGenres());
        } else {
            newFilm.setGenres(oldFilm.getGenres());
        }
        if (film.getMpa() != null) {
            if (ageRatingStorage.findAgeRatingById(film.getId()).isEmpty()) {
                throw new NotFoundException("Возрастная категория не найдена");
            }
            newFilm.setMpa(film.getMpa());
        } else {
            newFilm.setMpa(oldFilm.getMpa());
        }

        Film updateFilm = filmStorage.update(newFilm);
        log.trace("Данные фильма {}, айди {}, обновлены", updateFilm.getName(), updateFilm.getId());
        return updateFilm;
    }

    public Film findFilmById(int id) {
        return filmStorage.findFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public Film likeFilm(int filmId, int userId) {
        User optionalUser = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        film.like(userId);
        update(film);
        log.trace("Пользователь с айди {} оценил фильм с айди {} (количество лайков: {})",
                userId, filmId, film.getLikes().size());
        return film;
    }

    public Film dislikeFilm(int filmId, int userId) {
        User optionalUser = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        film.dislike(userId);
        update(film);
        log.trace("Пользователь с айди {} снял отметку \"нравится\" с фильма с айди {} (количество лайков: {})",
                userId, filmId, film.getLikes().size());
        return film;
    }

    public Collection<Film> getTop(int amount) {
        if (amount <= 0) {
            throw new ConditionsNotMetException("Количество позиций в топе не может быть меньше 1");
        }
        ArrayList<Film> filmTop = filmStorage.findAll().stream()
                .sorted(Comparator.comparing(Film::likeAmount).reversed())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
        List<Film> top10 = new ArrayList<>();
        if (!filmTop.isEmpty()) {
            for (int i = 0; i < amount && i < filmTop.size(); i++) {
                top10.add(filmTop.get(i));
            }
        }
        return top10;
    }
}
