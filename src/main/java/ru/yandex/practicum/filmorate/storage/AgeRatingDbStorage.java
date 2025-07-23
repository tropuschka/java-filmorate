package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.AgeRatingDbMapper;
import ru.yandex.practicum.filmorate.model.AgeRating;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("AgeRatingDbStorage")
@RequiredArgsConstructor
public class AgeRatingDbStorage implements AgeRatingStorage {
    @Autowired
    private final JdbcTemplate jdbc;
    @Autowired
    private final AgeRatingDbMapper ageRatingMapper;

    @Override
    public Collection<AgeRating> findAll() {
        String query = "SELECT * FROM age_ratings;";
        return jdbc.query(query, ageRatingMapper);
    }

    @Override
    public Optional<AgeRating> findAgeRatingById(int id) {
        try {
            String query = "SELECT * FROM age_ratings WHERE id = ?;";
            AgeRating result = jdbc.queryForObject(query, ageRatingMapper, id);
            return Optional.of(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

}
