package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return filmStorage.update(film);
    }

    @PostMapping("/{filmId}/like")
    public Film like(@RequestBody User user, @PathVariable Long filmId) {
        return FilmService.likeFilm(filmStorage, user, filmId);
    }

    @DeleteMapping("/{filmId}/like")
    public Film dislike(@RequestBody User user, @PathVariable Long filmId) {
        return FilmService.dislikeFilm(filmStorage, user, filmId);
    }

    @GetMapping("/top")
    public Collection<Film> getTop() {
        return FilmService.getTop(filmStorage);
    }
}
