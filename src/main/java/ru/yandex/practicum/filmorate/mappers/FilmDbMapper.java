package ru.yandex.practicum.filmorate.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("FilmDbMapper")
@RequiredArgsConstructor
public class FilmDbMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbc;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setAgeRatingId(resultSet.getInt("age_rating"));

        String genresSql = "SELECT genre_id FROM film_genres WHERE film_id = " + film.getId();
        ResultSetExtractor genreExtractor = new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                Set<Integer> genreList = new HashSet<>();
                while(rs.next()) {
                    Integer genre_id = rs.getInt("genre_id");
                    genreList.add(genre_id);
                }
                return genreList;
            }
        };
        film.setGenres((HashSet<Integer>) jdbc.query(genresSql, genreExtractor));

        String likesSql = "SELECT user_id FROM film_likes WHERE film_id = " + film.getId();
        ResultSetExtractor likesExtractor = new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                Set<Long> likesList = new HashSet<>();
                while(rs.next()) {
                    Long user_id = rs.getLong("user_id");
                    likesList.add(user_id);
                }
                return likesList;
            }
        };
        film.setLikes((HashSet<Long>) jdbc.query(likesSql, likesExtractor));

        return film;
    }
}
