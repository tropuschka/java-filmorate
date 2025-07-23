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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    @Autowired
    private final JdbcTemplate jdbc;
    @Autowired
    private final FilmDbMapper filmMapper;

    @Override
    public Collection<Film> findAll() {
        String query = "SELECT * FROM films;";
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
        updateLikes(film);

        String control = "SELECT * FROM films WHERE id = ?;";
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

        String likesQueryDelete = "DELETE FROM film_likes WHERE film_id = ?;";
        jdbc.update(likesQueryDelete, film.getId());
        updateLikes(film);

        String control = "SELECT * FROM films WHERE id = ?;";
        return jdbc.queryForObject(control, filmMapper, film.getId());
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        String control = "SELECT id, name, description, release_date, duration, age_rating FROM films WHERE id = ?;";
        List<Film> films = jdbc.query(control, filmMapper, id);
        if (films.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(films.getFirst());
        }
    }

    private void updatedGenres(Film film) {
        for (Genre genre:film.getGenres()) {
            Integer genreId = genre.getId();
            String genreQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES ( ?, ?);";
            jdbc.update(genreQuery, film.getId(), genreId);
        }
    }

    private void updateLikes(Film film) {
        for (Integer userId:film.getLikes()) {
            String likeQuery = "INSERT INTO film_likes (film_id, user_id) VALUES ( ?, ?);";
            jdbc.update(likeQuery, film.getId(), userId);
        }
    }
}
