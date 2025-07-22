package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final GenreService genreService;

    @Autowired
    public FilmController(FilmService filmService, GenreService genreService) {
        this.filmService = filmService;
        this.genreService = genreService;
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

    @GetMapping("/genres")
    public Collection<Genre> allGenres() {
        return genreService.findAll();
    }

    @GetMapping("/genres/{id}")
    public Genre findGenre(@PathVariable int id) {
        return genreService.findById(id);
    }
}
