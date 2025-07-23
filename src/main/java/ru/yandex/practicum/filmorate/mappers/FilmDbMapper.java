package ru.yandex.practicum.filmorate.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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

        if (resultSet.getInt("age_rating") != 0) {
            String mpaSql = "SELECT id, name, description FROM age_ratings WHERE id = ?;";
            film.setMpa(jdbc.queryForObject(mpaSql, ageRatingMapper, resultSet.getInt("age_rating")));
        }

        String genresSql = "SELECT genre_id FROM film_genres WHERE film_id = ?;";
        Set<Integer> genresIds = jdbc.queryForList(genresSql, Integer.class, film.getId()).stream()
                .collect(Collectors.toSet());
        Set<Genre> genres = new HashSet<>();
        for (Integer genreId:genresIds) {
            String genreSql = "SELECT * FROM genres WHERE id = ?;";
            List<Genre> genreList = jdbc.query(genreSql, genreMapper, genreId);
            if (!genreList.isEmpty()) {
                genres.add(genreList.getFirst());
            } else throw new NotFoundException("Жанр не найден");
        }
        film.setGenres(genres);

        String likesSql = "SELECT user_id FROM film_likes WHERE film_id = ?;";
        Set<Integer> likes = jdbc.queryForList(likesSql, Integer.class, film.getId()).stream().collect(Collectors.toSet());
        film.setLikes(likes);

        return film;
    }
}
