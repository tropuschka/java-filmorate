package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Film likeFilm(Long filmId, Long userId) {
        User optionalUser = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        Film film = filmStorage.findFilmById(filmId);
        film.like(userId);
        log.trace("Пользователь с айди {} оценил фильм с айди {} (количество лайков: {})",
                userId, filmId, film.getLikes().size());
        return film;
    }

    public Film dislikeFilm(Long filmId, Long userId) {
        User optionalUser = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        Film film = filmStorage.findFilmById(filmId);
        film.dislike(userId);
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
