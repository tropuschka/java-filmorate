package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTests extends FilmorateApplicationTests {
    @BeforeAll
    static void prepare() {
        dislikeFilm.setName("Dislike Film");
        filmController.create(dislikeFilm);
        filmController.like(dislikeFilm.getId(), me.getId());

        topFilm1.setName("Film 1");
        topFilmController.create(topFilm1);
        topFilmController.like(topFilm1.getId(), me.getId());

        topFilm2.setName("Film 2");
        topFilmController.create(topFilm2);

        topFilm3.setName("Film 3");
        topFilmController.create(topFilm3);
        topFilmController.like(topFilm3.getId(), me.getId());
        topFilmController.like(topFilm3.getId(), myFriend.getId());

        topFilm4.setName("Film 4");
        topFilmController.create(topFilm4);
        topFilmController.like(topFilm4.getId(), me.getId());
        topFilmController.like(topFilm4.getId(), myFriend.getId());
        topFilmController.like(topFilm4.getId(), othersFriend.getId());

        topFilm5.setName("Film 5");
        topFilmController.create(topFilm5);

        topFilm6.setName("Film 6");
        topFilmController.create(topFilm6);

        topFilm7.setName("Film 7");
        topFilmController.create(topFilm7);

        topFilm8.setName("Film 8");
        topFilmController.create(topFilm8);

        topFilm9.setName("Film 9");
        topFilmController.create(topFilm9);
    }

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

    @Test
    void likeFilm() {
        Film likeFilm = new Film();
        likeFilm.setName("Like Film");
        filmController.create(likeFilm);
        int filmLikes = likeFilm.getLikes().size();
        filmController.like(likeFilm.getId(), me.getId());
        assertEquals(filmLikes + 1, likeFilm.getLikes().size());
        assertTrue(likeFilm.getLikes().contains(me.getId()));
    }

    @Test
    void likeNoFilm() {
        Film likeFilm = new Film();
        likeFilm.setName("No Film");
        likeFilm.setId((long) filmController.findAll().size() + 1);
        NoSuchElementException notFoundException = assertThrows(NoSuchElementException.class,
                () -> filmController.like(likeFilm.getId(), me.getId()));
        assertEquals("No value present", notFoundException.getMessage());
        assertEquals(0, likeFilm.getLikes().size());
        assertFalse(likeFilm.getLikes().contains(me.getId()));
    }

    @Test
    void likeFilmNoUser() {
        User likeUser = new User();
        likeUser.setId((long) userStorage.findAll().size() + 1);
        likeUser.setEmail("like-film-no-user@mail.ru");
        int likeAmount = topFilm1.likeAmount();
        NoSuchElementException notFoundException = assertThrows(NoSuchElementException.class,
                () -> topFilmController.like(topFilm1.getId(), likeUser.getId()));
        assertEquals("No value present", notFoundException.getMessage());
        assertEquals(likeAmount, topFilm1.getLikes().size());
        assertFalse(topFilm1.getLikes().contains(likeUser.getId()));
    }

    @Test
    void dislikeFilm() {
        int likeAmount = dislikeFilm.getLikes().size();
        filmController.dislike(dislikeFilm.getId(), me.getId());
        assertEquals(likeAmount - 1, dislikeFilm.getLikes().size());
        assertFalse(dislikeFilm.getLikes().contains(me.getId()));
    }

    @Test
    void dislikeNoFilm() {
        Film dislikeNoFilm = new Film();
        dislikeNoFilm.setName("No Film");
        Long filmId = (long) filmController.findAll().size() + 1;
        dislikeNoFilm.setId(filmId);
        NoSuchElementException notFoundException = assertThrows(NoSuchElementException.class,
                () -> filmController.dislike(dislikeNoFilm.getId(), me.getId()));
        assertEquals("No value present", notFoundException.getMessage());
        assertEquals(0, dislikeNoFilm.getLikes().size());
    }

    @Test
    void dislikeFilmNoUser() {
        User likeUser = new User();
        likeUser.setId((long) userStorage.findAll().size() + 1);
        likeUser.setEmail("like-film-no-user@mail.ru");
        int likeAmount = topFilm1.likeAmount();
        NoSuchElementException notFoundException = assertThrows(NoSuchElementException.class,
                () -> topFilmController.dislike(topFilm1.getId(), likeUser.getId()));
        assertEquals("No value present", notFoundException.getMessage());
        assertEquals(likeAmount, topFilm1.getLikes().size());
        assertFalse(topFilm1.getLikes().contains(likeUser.getId()));
    }

    @Test
    void getFilmTop9() {
        List<Film> filmTop = (List<Film>) topFilmController.getTop(10);
        assertEquals(9, filmTop.size());
        assertEquals(filmTop.getFirst(), topFilm4);
        assertEquals(filmTop.get(1), topFilm3);
        assertEquals(filmTop.get(2), topFilm1);
    }

    @Test
    void getFilmTop10() {
        FilmStorage top10FilmStorage = new InMemoryFilmStorage();
        FilmController top10FilmController = new FilmController(top10FilmStorage, userController);
        for (Film film : topFilmStorage.findAll()) {
            top10FilmController.create(film);
        }
        top10FilmController.create(film);
        List<Film> filmTop = (List<Film>) top10FilmController.getTop(10);
        assertEquals(10, filmTop.size());
        assertEquals(filmTop.getFirst(), topFilm4);
        assertEquals(filmTop.get(1), topFilm3);
        assertEquals(filmTop.get(2), topFilm1);
    }

    @Test
    void getFilmTop11() {
        FilmStorage top11FilmStorage = new InMemoryFilmStorage();
        FilmController top11FilmController = new FilmController(top11FilmStorage, userController);
        for (Film film : topFilmStorage.findAll()) {
            top11FilmController.create(film);
        }
        Film film11 = new Film();
        film11.setName("Film 11");
        top11FilmController.create(film);
        top11FilmController.create(film11);
        ArrayList<Film> filmTop = new ArrayList<>(top11FilmController.getTop(10));
        assertEquals(10, filmTop.size());
        assertEquals(filmTop.getFirst(), topFilm4);
        assertEquals(filmTop.get(1), topFilm3);
        assertEquals(filmTop.get(2), topFilm1);
    }

    @Test
    void getFilmTop3() {
        List<Film> filmTop = (List<Film>) topFilmController.getTop(3);
        assertEquals(3, filmTop.size());
        assertEquals(filmTop.getFirst(), topFilm4);
        assertEquals(filmTop.get(1), topFilm3);
        assertEquals(filmTop.get(2), topFilm1);
    }

    @Test
    void getFilmTop0() {
        ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
                () -> topFilmController.getTop(0));
        assertEquals("Количество позиций в топе не может быть меньше 1",
                conditionsNotMetException.getMessage());
    }

    @Test
    void getFilmTopNegative() {
        ConditionsNotMetException conditionsNotMet = assertThrows(ConditionsNotMetException.class,
                () -> topFilmController.getTop(-1));
        assertEquals("Количество позиций в топе не может быть меньше 1", conditionsNotMet.getMessage());
    }

    @Test
    void getEmptyTop() {
        FilmStorage emptyFilmStorage = new InMemoryFilmStorage();
        FilmController emptyFilmController = new FilmController(emptyFilmStorage, userController);
        List<Film> filmTop = (List<Film>) emptyFilmController.getTop(10);
        assertEquals(0, filmTop.size());
    }

    @Test
    void getAllFilms() {
        Film film2 = film;
        film2.setName("Film");
        filmController.create(film);
        filmController.create(film2);

        Collection<Film> controllerFilmList = filmController.findAll();

        assertNotNull(controllerFilmList);
        assertEquals(filmStorage.findAll().size(), controllerFilmList.size());
    }
}
