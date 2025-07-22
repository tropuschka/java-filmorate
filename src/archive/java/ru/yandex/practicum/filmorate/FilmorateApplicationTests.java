/*
package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.time.Month;

@SpringBootTest
class FilmorateApplicationTests {
	static Film film = new Film();
	static User user = new User();
	static FilmStorage filmStorage = new InMemoryFilmStorage();
	static UserStorage userStorage = new InMemoryUserStorage();
	static UserService userService = new UserService(userStorage);
	static FilmService filmService = new FilmService(userStorage, filmStorage);
	static UserController userController = new UserController(userService);
	static FilmController filmController = new FilmController(filmService);

	@BeforeAll
	static void prepare() {
		film.setName("Name");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1));
		film.setDuration(45);

		user.setLogin("Login");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));
		user.setName("Name");
	}
}
*/