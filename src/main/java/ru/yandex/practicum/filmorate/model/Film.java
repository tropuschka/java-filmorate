package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@Setter
public class Film {
    int id;
    String name;
    String description;
    LocalDate releaseDate;
    Duration duration;
}
