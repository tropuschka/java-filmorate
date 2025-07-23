package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.AgeRating;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

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
        Optional<Film> filmOptional = filmStorage.findFilmById(1);

        assertThat(filmOptional)
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

    @Test
    public void testGetAllGenres() {
        List<Genre> dbGenreList = (List<Genre>) genreStorage.findAll();
        assertEquals(1, dbGenreList.size());
    }

    @Test
    public void testFindGenreById() {
        Optional<Genre> genreOptional = genreStorage.findGenreById(1);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testGetAllMpa() {
        List<AgeRating> dbMpaList = (List<AgeRating>) ageRatingStorage.findAll();
        assertEquals(1, dbMpaList.size());
    }

    @Test
    public void testFindMpaById() {
        Optional<AgeRating> mpaOptional = ageRatingStorage.findAgeRatingById(1);

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1)
                );
    }
}
