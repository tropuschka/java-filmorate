package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.GenreDbMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("GenreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    @Autowired
    private final JdbcTemplate jdbc;
    @Autowired
    private final GenreDbMapper genreMapper;

    @Override
    public Collection<Genre> findAll() {
        String query = "SELECT * FROM genres;";
        return jdbc.query(query, genreMapper);
    }

    @Override
    public Optional<Genre> findGenreById(int id) {
        String query = "SELECT * FROM genres WHERE id = ?;";
        List<Genre> genres = jdbc.query(query, genreMapper, id);
        if (genres.isEmpty()) return Optional.empty();
        return Optional.ofNullable(jdbc.queryForObject(query, genreMapper, id));
    }
}
