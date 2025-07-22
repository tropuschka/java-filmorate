package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.GenreDbMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Repository("GenreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    @Autowired
    private final JdbcTemplate jdbc;
    @Autowired
    private final GenreDbMapper genreMapper;
}
