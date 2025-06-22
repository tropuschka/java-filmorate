package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    public static Film likeFilm(FilmStorage filmStorage, User user, Long filmId) {
        Optional<Film> optionalFilm = filmStorage.findFilmById(filmId);
        if (optionalFilm.isEmpty()) {
            ExceptionService.throwNotFoundException("Фильм не найден");
        }
        Film film = optionalFilm.get();
        if (film.getLikes().contains(user.getId())) {
            ExceptionService.throwDuplicationException("Лайк уже поставлен");
        }
        film.like(user.getId());
        log.trace("Пользователь {} оценил фильм {} (количество лайков: {})",
                user.getName(), film.getName(), film.getLikes().size());
        return film;
    }

    public static Film dislikeFilm(FilmStorage filmStorage, User user, Long filmId) {
        Optional<Film> optionalFilm = filmStorage.findFilmById(filmId);
        if (optionalFilm.isEmpty()) {
            ExceptionService.throwNotFoundException("Фильм не найден");
        }
        Film film = optionalFilm.get();
        if (!film.getLikes().contains(user.getId())) {
            ExceptionService.throwValidationException("Пользователь " + user.getName()
                    + "не оценивал фильм " + film.getName());
        }
        film.dislike(user.getId());
        return film;
    }

    public static Collection<Film> getTop(FilmStorage filmStorage) {
        ArrayList<Film> filmTop = filmStorage.findAll().stream()
                .sorted(Comparator.comparing(Film::likeAmount).reversed())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
        List<Film> top10 = new ArrayList<>();
        if (!filmTop.isEmpty()) {
            for (int i = 0; i < 10 && i < filmTop.size(); i++) {
                top10.add(filmTop.get(i));
            }
        }
        return top10;
    }
}
