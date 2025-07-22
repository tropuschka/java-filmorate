package ru.yandex.practicum.filmorate.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("FilmDbMapper")
@RequiredArgsConstructor
public class FilmDbMapper implements RowMapper<Film> {
    @Autowired
    private final JdbcTemplate jdbc;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        if (resultSet.getDate("release_date") != null) {
            film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        }
        film.setDuration(resultSet.getInt("duration"));
        film.setAgeRatingId(resultSet.getInt("age_rating"));

        String genresSql = "SELECT genre_id FROM film_genres WHERE film_id = ?;";
        Set<Integer> genres = jdbc.queryForList(genresSql, Integer.class, film.getId()).stream()
                .collect(Collectors.toSet());
        film.setGenres(genres);

        String likesSql = "SELECT user_id FROM film_likes WHERE film_id = ?;";
        Set<Integer> likes = jdbc.queryForList(likesSql, Integer.class, film.getId()).stream().collect(Collectors.toSet());
        film.setLikes(likes);

        return film;
    }
}
