package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
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
    public static Film likeFilm(FilmStorage filmStorage, UserStorage userStorage, Long filmId, Long userId) {
        Optional<Film> optionalFilm = filmStorage.findFilmById(filmId);
        if (optionalFilm.isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }
        Optional<User> optionalUser = userStorage.findUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Film film = optionalFilm.get();
        film.like(userId);
        log.trace("Пользователь с айди {} оценил фильм с айди {} (количество лайков: {})",
                userId, filmId, film.getLikes().size());
        return film;
    }

    public static Film dislikeFilm(FilmStorage filmStorage, UserStorage userStorage, Long filmId, Long userId) {
        Optional<Film> optionalFilm = filmStorage.findFilmById(filmId);
        if (optionalFilm.isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }
        Optional<User> optionalUser = userStorage.findUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Film film = optionalFilm.get();
        film.dislike(userId);
        return film;
    }

    public static Collection<Film> getTop(FilmStorage filmStorage, int amount) {
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
