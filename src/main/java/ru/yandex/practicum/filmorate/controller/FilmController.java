package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final UserController userController;

    @Autowired
    public FilmController(FilmStorage filmStorage, UserController userController) {
        this.filmStorage = filmStorage;
        this.userController = userController;
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

    @PutMapping("/{id}/like/{userId}")
    public Film like(@PathVariable Long id, @PathVariable Long userId) {
        return FilmService.likeFilm(filmStorage, userController, id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film dislike(@PathVariable Long id, @PathVariable Long userId) {
        return FilmService.dislikeFilm(filmStorage, userController, id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTop(@RequestParam(defaultValue = "10") int count) {
        return FilmService.getTop(filmStorage, count);
    }
}
