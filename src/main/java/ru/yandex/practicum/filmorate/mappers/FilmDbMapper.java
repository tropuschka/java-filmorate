package ru.yandex.practicum.filmorate.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.AgeRating;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("FilmDbMapper")
@RequiredArgsConstructor
public class FilmDbMapper implements RowMapper<Film> {
    @Autowired
    private final JdbcTemplate jdbc;
    @Autowired
    private final AgeRatingDbMapper ageRatingMapper;
    @Autowired
    private final GenreDbMapper genreMapper;

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

        if (resultSet.getInt("mpa_id") != 0) {
            AgeRating ageRating = new AgeRating();
            ageRating.setId(resultSet.getInt("mpa_id"));
            ageRating.setName(resultSet.getString("mpa_name"));
            ageRating.setDescription(resultSet.getString("mpa_description"));
            film.setMpa(ageRating);
        }

        String genresSql = "SELECT * FROM genres WHERE id IN " +
                "(SELECT genre_id FROM film_genres WHERE film_id = ?) ORDER BY id;";
        List<Genre> genres = jdbc.query(genresSql, genreMapper, film.getId());
        Set<Genre> genreSet = new TreeSet<>(Comparator.comparing(Genre::getId));
        genreSet.addAll(genres);
        film.setGenres(genreSet);

        return film;
    }
}
