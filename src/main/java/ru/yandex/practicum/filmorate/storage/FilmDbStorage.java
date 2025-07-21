package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.FilmDbMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

@Repository("FilmDbMapper")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbc;
    private final FilmDbMapper filmMapper;

    @Override
    public Collection<Film> findAll() {
        String query = "SELECT * FROM films;";
        return jdbc.query(query, filmMapper);
    }

    @Override
    public Film create(Film film) {
        StringBuilder query = new StringBuilder("INSERT INTO films " +
                "(id, name, description, release_date, duration, age_rating) VALUES ?, ?, ?, ?, ?, ?;");
        for (Integer genre_id:film.getGenres()) {
            query.append("INSERT INTO film_genres (film_id, genre_id) VALUES ").append(film.getId()).append(", ")
                    .append(genre_id).append(";");
        }
        for (Long user_id:film.getLikes()) {
            query.append("INSERT INTO film_likes (film_id, user_id) VALUES ").append(film.getId()).append(", ")
                    .append(user_id).append(";");
        }
        jdbc.update(query.toString(), film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getAgeRatingId());
        String control = "SELECT * FROM films WHERE id = " + film.getId();
        return jdbc.queryForObject(control, filmMapper);
    }

    @Override
    public Film update(Film film) {
        String query = "INSERT INTO films (id, name, description, release_date, duration, age_rating) " +
                "VALUES ?, ?, ?, ?, ?, ?;";
        jdbc.update(query, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getAgeRatingId());
        String control = "SELECT * FROM films WHERE id = " + film.getId();
        return jdbc.queryForObject(control, filmMapper);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        String query = "SELECT * FROM films WHERE id = " + id + ";";
        return Optional.ofNullable(jdbc.queryForObject(query, filmMapper));
    }
}
