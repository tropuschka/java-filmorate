/*
package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTests extends FilmorateApplicationTests {
    @Test
    void createFilm() {
        Film newFilm = filmController.create(film);

        assertNotNull(newFilm);
        assertEquals(film.getName(), newFilm.getName());
        assertEquals(film.getDescription(), newFilm.getDescription());
        assertEquals(film.getReleaseDate(), newFilm.getReleaseDate());
        assertEquals(film.getDuration(), newFilm.getDuration());
    }

    @Test
    void createFilmNoName() {
        Film corruptFilm = new Film();

        ConditionsNotMetException conditionsNotMet = assertThrows(ConditionsNotMetException.class,
                () -> filmController.create(corruptFilm));
        assertEquals("Название должно быть указано", conditionsNotMet.getMessage());
    }

    @Test
    void createFilmLongDescription() {
        Film corruptFilm = new Film();
        corruptFilm.setName("LongDescription");
        String description = "Друзья играют в мафию, но по какой-то причине не успевают закончить партию. " +
                "Через некоторое время один из игравших сообщает другому, что кто-то решил продолжить игру. " +
                "(дыра в завязке - надо было сразу звонить ментам)";
        corruptFilm.setDescription(description);

        ConditionsNotMetException conditionsNotMet = assertThrows(ConditionsNotMetException.class,
                () -> filmController.create(corruptFilm));
        assertEquals("Описание не должно быть длиннее 200 символов", conditionsNotMet.getMessage());
    }

    @Test
    void createFilmEarlyDate() {
        Film corruptFilm = new Film();
        corruptFilm.setName("EarlyDate");
        corruptFilm.setReleaseDate(LocalDate.of(1895, Month.NOVEMBER, 20));

        ConditionsNotMetException conditionsNotMet = assertThrows(ConditionsNotMetException.class,
                () -> filmController.create(corruptFilm));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1885 года", conditionsNotMet.getMessage());
    }

    @Test
    void createFilmDurationNull() {
        Film corruptFilm = new Film();
        corruptFilm.setName("DurationNull");
        corruptFilm.setDuration(0);

        ConditionsNotMetException conditionsNotMet = assertThrows(ConditionsNotMetException.class,
                () -> filmController.create(corruptFilm));
        assertEquals("Длительность фильма должна быть положительной", conditionsNotMet.getMessage());
    }

    @Test
    void createFilmDurationNegative() {
        Film corruptFilm = new Film();
        corruptFilm.setName("DurationNegative");
        corruptFilm.setDuration(-5);

        ConditionsNotMetException conditionsNotMet = assertThrows(ConditionsNotMetException.class,
                () -> filmController.create(corruptFilm));
        assertEquals("Длительность фильма должна быть положительной", conditionsNotMet.getMessage());
    }

    @Test
    void changeFilm() {
        Long filmId = filmController.create(film).getId();
        Film filmUpdate = new Film();
        filmUpdate.setId(filmId);
        filmUpdate.setName("New name");
        Film newFilm = filmController.update(filmUpdate);

        assertNotNull(newFilm);
        assertEquals(filmUpdate.getName(), newFilm.getName());
        assertEquals(film.getDescription(), newFilm.getDescription());
        assertEquals(film.getReleaseDate(), newFilm.getReleaseDate());
        assertEquals(film.getDuration(), newFilm.getDuration());
    }

    @Test
    void changeFilmNoId() {
        Film filmUpdate = new Film();
        filmUpdate.setName("New name");

        ConditionsNotMetException conditionsNotMet = assertThrows(ConditionsNotMetException.class,
                () -> filmController.update(filmUpdate));
        assertEquals("Id должен быть указан", conditionsNotMet.getMessage());
    }

    @Test
    void changeFilmNotFound() {
        Long filmId = filmController.create(film).getId();
        Film filmUpdate = new Film();
        filmUpdate.setId(filmId + 1);
        filmUpdate.setName("New name");

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> filmController.update(filmUpdate));
        assertEquals("Фильм не найден", notFoundException.getMessage());
    }

    @Test
    void changeFilmLongDescription() {
        Long filmId = filmController.create(film).getId();
        Film filmUpdate = new Film();
        filmUpdate.setId(filmId);
        String description = "Друзья играют в мафию, но по какой-то причине не успевают закончить партию. " +
                "Через некоторое время один из игравших сообщает другому, что кто-то решил продолжить игру. " +
                "(дыра в завязке - надо было сразу звонить ментам)";
        filmUpdate.setDescription(description);

        ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
                () -> filmController.update(filmUpdate));
        assertEquals("Описание не должно быть длиннее 200 символов", conditionsNotMetException.getMessage());
    }

    @Test
    void changeFilmDurationNull() {
        Long filmId = filmController.create(film).getId();
        Film filmUpdate = new Film();
        filmUpdate.setId(filmId);
        filmUpdate.setDuration(0);

        ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
                () -> filmController.update(filmUpdate));
        assertEquals("Длительность фильма должна быть положительной", conditionsNotMetException.getMessage());
    }

    @Test
    void changeFilmDurationNegative() {
        Long filmId = filmController.create(film).getId();
        Film filmUpdate = new Film();
        filmUpdate.setId(filmId);
        filmUpdate.setDuration(-5);

        ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
                () -> filmController.update(filmUpdate));
        assertEquals("Длительность фильма должна быть положительной", conditionsNotMetException.getMessage());
    }

    @Test
    void changeFilmEarlyDate() {
        Long filmId = filmController.create(film).getId();
        Film filmUpdate = new Film();
        filmUpdate.setId(filmId);
        filmUpdate.setReleaseDate(LocalDate.of(1895, Month.NOVEMBER, 20));

        ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
                () -> filmController.update(filmUpdate));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1885 года",
                conditionsNotMetException.getMessage());
    }
}
*/