package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTests extends FilmorateApplicationTests {
    @Test
    public void testCreateFilm() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Some test film");
        film.setDuration(45);
        film.setAgeRatingId(2);
        film.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1));

        Optional<Film> filmOptional = Optional.of(filmStorage.create(film));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(dbFilm ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "Test")
                );
    }

    @Test
    public void testFindFilmById() {
        Optional<Film> userOptional = filmStorage.findFilmById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setId(1);
        film.setName("Nina");
        Optional<Film> filmOptional = Optional.of(filmStorage.update(film));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(dbFilm ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "Nina")
                );
    }

    @Test
    public void testGetAllFilms() {
        List<Film> dbFilmList = (List<Film>) filmStorage.findAll();
        assertEquals(1, dbFilmList.size());
    }
}
