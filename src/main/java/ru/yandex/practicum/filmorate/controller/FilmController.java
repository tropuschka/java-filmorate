package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable int id) {
        return filmService.findFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film like(@PathVariable int id, @PathVariable int userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film dislike(@PathVariable int id, @PathVariable int userId) {
        return filmService.dislikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTop(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTop(count);
    }
}
