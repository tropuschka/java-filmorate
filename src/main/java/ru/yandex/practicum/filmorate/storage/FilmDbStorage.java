package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.FilmDbMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    @Autowired
    private final JdbcTemplate jdbc;
    @Autowired
    private final FilmDbMapper filmMapper;

    @Override
    public Collection<Film> findAll() {
        String query = "SELECT f.id id, f.name name, f.description description, " +
                "f.release_date release_date, f.duration duration, " +
                "mpa.id mpa_id, mpa.name mpa_name, mpa.description mpa_description FROM films f " +
                "LEFT JOIN age_ratings mpa ON f.age_rating = mpa.id;";
        return jdbc.query(query, filmMapper);
    }

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder newId = new GeneratedKeyHolder();
        String query = "INSERT INTO films " +
                "(name, description, release_date, duration, age_rating) VALUES (?, ?, ?, ?, ?);";
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, film.getName());
            ps.setObject(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setObject(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, newId);
        Integer filmId = Objects.requireNonNull(newId.getKey()).intValue();
        film.setId(filmId);
        updatedGenres(film);

        String control = "SELECT f.id id, f.name name, f.description description, " +
                "f.release_date release_date, f.duration duration, " +
                "mpa.id mpa_id, mpa.name mpa_name, mpa.description mpa_description FROM films f " +
                "LEFT JOIN age_ratings mpa ON f.age_rating = mpa.id WHERE f.id = ?;";
        Film dbFilm = jdbc.queryForObject(control, filmMapper, filmId);
        return dbFilm;
    }

    @Override
    public Film update(Film film) {
        String query = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "age_rating = ? WHERE id = ?;";
        jdbc.update(query, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        String genreQueryDelete = "DELETE FROM film_genres WHERE film_id = ?;";
        jdbc.update(genreQueryDelete, film.getId());
        updatedGenres(film);

        String control = "SELECT f.id id, f.name name, f.description description, " +
                "f.release_date release_date, f.duration duration, " +
                "mpa.id mpa_id, mpa.name mpa_name, mpa.description mpa_description FROM films f " +
                "LEFT JOIN age_ratings mpa ON f.age_rating = mpa.id WHERE f.id = ?;";
        return jdbc.queryForObject(control, filmMapper, film.getId());
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        String control = "SELECT f.id id, f.name name, f.description description, " +
                "f.release_date release_date, f.duration duration, " +
                "mpa.id mpa_id, mpa.name mpa_name, mpa.description mpa_description FROM films f " +
                "LEFT JOIN age_ratings mpa ON f.age_rating = mpa.id WHERE f.id = ?;";
        List<Film> films = jdbc.query(control, filmMapper, id);
        if (films.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(films.getFirst());
        }
    }

    public Collection<Film> getTop(int amount) {
        String sql = "SELECT f.id id, f.name name, f.description description, " +
                "f.release_date release_date, f.duration duration, " +
                "mpa.id mpa_id, mpa.name mpa_name, mpa.description mpa_description FROM films f " +
                "LEFT JOIN age_ratings mpa ON f.age_rating = mpa.id " +
                "LEFT JOIN (SELECT film_id, COUNT(user_id) film_rate FROM film_likes " +
                "GROUP BY film_id) rate " +
                "ON f.id=rate.film_id " +
                "ORDER BY rate.film_rate DESC, f.id LIMIT ?;";
        return jdbc.query(sql, filmMapper, amount);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String likeQuery = "INSERT INTO film_likes (film_id, user_id) VALUES ( ?, ?);";
        jdbc.update(likeQuery, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String likesQueryDelete = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?;";
        jdbc.update(likesQueryDelete, filmId, userId);
    }

    private void updatedGenres(Film film) {
        String genreQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES ( ?, ?);";
        jdbc.batchUpdate(genreQuery, film.getGenres(), film.getId(), (PreparedStatement ps, Genre genre) -> {
            ps.setInt(1, film.getId());
            ps.setInt(2, genre.getId());
        });
    }
}
