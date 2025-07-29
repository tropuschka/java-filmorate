package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.AgeRating;
import ru.yandex.practicum.filmorate.service.AgeRatingService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final AgeRatingService ageRatingService;

    @Autowired
    public MpaController(AgeRatingService ageRatingService) {
        this.ageRatingService = ageRatingService;
    }

    @GetMapping
    public Collection<AgeRating> allMpa() {
        return ageRatingService.findAll();
    }

    @GetMapping("/{id}")
    public AgeRating findMpa(@PathVariable int id) {
        return ageRatingService.findById(id);
    }
}
