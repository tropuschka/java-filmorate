package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    public static Film likeFilm(FilmStorage filmStorage, User user, Long FilmId) {
        Optional<Film> optionalFilm = filmStorage.findFilmById(FilmId);
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
}
