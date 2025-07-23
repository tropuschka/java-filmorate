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
        String queryNext = "VALUES NEXT VALUE FOR film_sequence;";
        Integer newId = jdbc.queryForObject(queryNext, Integer.class);
        StringBuilder query = new StringBuilder("INSERT INTO films " +
                "(id,name, description, release_date, duration, age_rating) VALUES (?,?, ?, ?, ?, ?);");
        Integer mpaId = null;
        if (film.getMpa() != null) mpaId = film.getMpa().getId();
        jdbc.update(query.toString(), newId, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), mpaId);
        updatedGenres(film);
        updateLikes(film);

        String control = "SELECT * FROM films WHERE id = ?;";
        Film dbFilm = jdbc.queryForObject(control, filmMapper, newId);
        return dbFilm;
    }

    @Override
    public Film update(Film film) {
        String query = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "age_rating = ? WHERE id = ?;";
        jdbc.update(query, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa(), film.getId());

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
        String control = "SELECT * FROM films;";
        Collection<Film> check = jdbc.query(control, filmMapper);
        control = "SELECT * FROM films WHERE id = ?;";
        Film film = jdbc.queryForObject(control, filmMapper, id);
        Optional<Film> optFilm = Optional.ofNullable(film);
        return optFilm;
    }

    private void updatedGenres(Film film) {
        for (Integer genreId :film.getGenres()) {
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
