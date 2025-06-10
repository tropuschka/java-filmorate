package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FilmorateApplicationTests {
	static Film film = new Film();
	static User user = new User();
	static FilmController filmController = new FilmController();
	static UserController userController = new UserController();

	@BeforeAll
    static void prepare() {
		film.setName("Name");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1));
		film.setDuration(Duration.ofMinutes(45));

		user.setLogin("Login");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));
		user.setName("Name");
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
	void changeFilm() {
		Integer filmId = filmController.create(film).getId();
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
	void getAllFilms() {
		Film film2 = film;
		film2.setName("Film");
		filmController.create(film);
		filmController.create(film2);

		Collection<Film> controllerFilmList = filmController.findAll();

		assertNotNull(controllerFilmList);
		assertEquals(controllerFilmList.size(), 3);
	}

	@Test
	void createUser() {
		user.setLogin("UserLogin");
		user.setEmail("user3000@gmail.com");
		User newUser = userController.create(user);

		assertNotNull(newUser);
		assertEquals(user.getName(), newUser.getName());
		assertEquals(user.getLogin(), newUser.getLogin());
		assertEquals(user.getBirthday(), newUser.getBirthday());
		assertEquals(user.getEmail(), newUser.getEmail());
	}

	@Test
	void changeUser() {
		Integer userId = userController.create(user).getId();
		User userUpdate = new User();
		userUpdate.setId(userId);
		userUpdate.setName("New name");
		User newUser = userController.update(userUpdate);

		assertNotNull(newUser);
		assertEquals(userUpdate.getName(), newUser.getName());
		assertEquals(user.getLogin(), newUser.getLogin());
		assertEquals(user.getBirthday(), newUser.getBirthday());
		assertEquals(user.getEmail(), newUser.getEmail());
	}

	@Test
	void getAllUsers() {
		User user2 = new User();
		user2.setLogin("User");
		user2.setEmail("user2@mail.ru");
		userController.create(user2);

		Collection<User> controllerUserList = userController.findAll();

		assertNotNull(controllerUserList);
		assertEquals(controllerUserList.size(), 2);
	}
}
