package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.FilmDbMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

@Repository("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage{
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
        StringBuilder query = new StringBuilder("INSERT INTO films " +
                "(name, description, release_date, duration, age_rating) VALUES (?, ?, ?, ?, ?);");
        jdbc.update(query.toString(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getAgeRatingId());
        updatedGenres(film);
        updateLikes(film);

        Integer filmDbId = jdbc.queryForObject("SELECT id FROM films WHERE name = ?, description = ?, " +
                        "release_date = ?, duration = ?, age_rating = ?;", Integer.class,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getAgeRatingId());

        String control = "SELECT * FROM films WHERE id = ?;";
        return jdbc.queryForObject(control, filmMapper, filmDbId);
    }

    @Override
    public Film update(Film film) {
        String query = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "age_rating = ? WHERE id = ?;";
        jdbc.update(query, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getAgeRatingId(), film.getId());

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
        String query = "SELECT * FROM films WHERE id = ?;";
        return Optional.ofNullable(jdbc.queryForObject(query, filmMapper, id));
    }

    private void updatedGenres(Film film) {
        for (Integer genre_id:film.getGenres()) {
            String genreQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES ( ?, ?);";
            jdbc.update(genreQuery, film.getId(), genre_id);
        }
    }

    private void updateLikes(Film film) {
        for (Integer user_id:film.getLikes()) {
            String likeQuery = "INSERT INTO film_likes (film_id, user_id) VALUES ( ?, ?);";
            jdbc.update(likeQuery, film.getId(), user_id);
        }
    }
}
