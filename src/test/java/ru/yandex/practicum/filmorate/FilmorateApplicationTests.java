package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

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
		film.setDuration(45);

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
	void createFilmNoName() {
		Film corruptFilm = new Film();

		assertThrows(ConditionsNotMetException.class, () -> filmController.create(corruptFilm));
	}

	@Test
	void createFilmLongDescription() {
		Film corruptFilm = new Film();
		corruptFilm.setName("LongDescription");
		String description = "Друзья играют в мафию, но по какой-то причине не успевают закончить партию. " +
				"Через некоторое время один из игравших сообщает другому, что кто-то решил продолжить игру. " +
				"(дыра в завязке - надо было сразу звонить ментам)";
		corruptFilm.setDescription(description);

		assertThrows(ConditionsNotMetException.class, () -> filmController.create(corruptFilm));
	}

	@Test
	void createFilmEarlyDate() {
		Film corruptFilm = new Film();
		corruptFilm.setName("EarlyDate");
		corruptFilm.setReleaseDate(LocalDate.of(1895, Month.NOVEMBER, 20));

		assertThrows(ConditionsNotMetException.class, () -> filmController.create(corruptFilm));
	}

	@Test
	void createFilmDurationNull() {
		Film corruptFilm = new Film();
		corruptFilm.setName("DurationNull");
		corruptFilm.setDuration(0);

		assertThrows(ConditionsNotMetException.class, () -> filmController.create(corruptFilm));
	}

	@Test
	void createFilmDurationNegative() {
		Film corruptFilm = new Film();
		corruptFilm.setName("DurationNegative");
		corruptFilm.setDuration(-5);

		assertThrows(ConditionsNotMetException.class, () -> filmController.create(corruptFilm));
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
	void changeFilmNoId() {
		Film filmUpdate = new Film();
		filmUpdate.setName("New name");

		assertThrows(ConditionsNotMetException.class, () -> filmController.update(filmUpdate));
	}

	@Test
	void changeFilmNotFound() {
		Integer filmId = filmController.create(film).getId();
		Film filmUpdate = new Film();
		filmUpdate.setId(filmId + 1);
		filmUpdate.setName("New name");

		assertThrows(NotFoundException.class, () -> filmController.update(filmUpdate));
	}

	@Test
	void changeFilmLongDescription() {
		Integer filmId = filmController.create(film).getId();
		Film filmUpdate = new Film();
		filmUpdate.setId(filmId);
		String description = "Друзья играют в мафию, но по какой-то причине не успевают закончить партию. " +
				"Через некоторое время один из игравших сообщает другому, что кто-то решил продолжить игру. " +
				"(дыра в завязке - надо было сразу звонить ментам)";
		filmUpdate.setDescription(description);

		assertThrows(ConditionsNotMetException.class, () -> filmController.update(filmUpdate));
	}

	@Test
	void changeFilmDurationNull() {
		Integer filmId = filmController.create(film).getId();
		Film filmUpdate = new Film();
		filmUpdate.setId(filmId);
		filmUpdate.setDuration(0);

		assertThrows(ConditionsNotMetException.class, () -> filmController.update(filmUpdate));
	}

	@Test
	void changeFilmDurationNegative() {
		Integer filmId = filmController.create(film).getId();
		Film filmUpdate = new Film();
		filmUpdate.setId(filmId);
		filmUpdate.setDuration(-5);

		assertThrows(ConditionsNotMetException.class, () -> filmController.update(filmUpdate));
	}

	@Test
	void changeFilmEarlyDate() {
		Integer filmId = filmController.create(film).getId();
		Film filmUpdate = new Film();
		filmUpdate.setId(filmId);
		filmUpdate.setReleaseDate(LocalDate.of(1895, Month.NOVEMBER, 20));

		assertThrows(ConditionsNotMetException.class, () -> filmController.update(filmUpdate));
	}

	@Test
	void getAllFilms() {
		Film film2 = film;
		film2.setName("Film");
		filmController.create(film);
		filmController.create(film2);

		Collection<Film> controllerFilmList = filmController.findAll();

		assertNotNull(controllerFilmList);
		assertEquals(controllerFilmList.size(), 4);
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
	void createUserNoEmail() {
		User curruptUser = new User();
		curruptUser.setLogin("CurruptedUser");
		curruptUser.setEmail("");

		assertThrows(ConditionsNotMetException.class, () -> userController.create(curruptUser));
	}

	@Test
	void createUserWrongEmail() {
		User curruptUser = new User();
		curruptUser.setLogin("CurruptedUser");
		curruptUser.setEmail("mail");

		assertThrows(ConditionsNotMetException.class, () -> userController.create(curruptUser));
	}

	@Test
	void createUserDuplicatedEmail() {
		User ok_user = new User();
		ok_user.setLogin("OkUser");
		ok_user.setEmail("ok_mail@mail.ru");
		userController.create(ok_user);
		User curruptUser = new User();
		curruptUser.setLogin("CurruptedUser");
		curruptUser.setEmail("ok_mail@mail.ru");

		assertThrows(DuplicatedDataException.class, () -> userController.create(curruptUser));
	}

	@Test
	void createUserNoLogin() {
		System.out.println(userController.findAll());
		User curruptUser = new User();
		curruptUser.setLogin("");
		curruptUser.setEmail("currupted_user@mail.ru");

		assertThrows(ConditionsNotMetException.class, () -> userController.create(curruptUser));
	}

	@Test
	void createUserLoginWithSpaces() {
		User curruptUser = new User();
		curruptUser.setLogin("Currupted User");
		curruptUser.setEmail("currupted_user@mail.ru");

		assertThrows(ConditionsNotMetException.class, () -> userController.create(curruptUser));
	}

	@Test
	void createUserDuplicatedLogin() {
		System.out.println(userController.findAll());
		User ok_user = new User();
		ok_user.setLogin("Ok_User");
		ok_user.setEmail("okmail@mail.ru");
		userController.create(ok_user);
		User curruptUser = new User();
		curruptUser.setLogin("Ok_User");
		curruptUser.setEmail("currupted_user@mail.ru");

		assertThrows(DuplicatedDataException.class, () -> userController.create(curruptUser));
	}

	@Test
	void createUserLateBirthday() {
		User curruptUser = new User();
		curruptUser.setLogin("Currupted User");
		curruptUser.setEmail("currupted_user@mail.ru");
		curruptUser.setBirthday(LocalDate.now().plusDays(5));

		assertThrows(ConditionsNotMetException.class, () -> userController.create(curruptUser));
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
	void changeUserNoId() {
		User userUpdate = new User();
		userUpdate.setName("New name");

		assertThrows(ConditionsNotMetException.class, () -> userController.update(userUpdate));
	}

	@Test
	void changeUserNotFound() {
		User userToUpdate = new User();
		userToUpdate.setEmail("not_found@mail.ru");
		userToUpdate.setLogin("Not_Found");
		Integer userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId + 1);
		userUpdate.setName("New name");

		assertThrows(NotFoundException.class, () -> userController.update(userUpdate));
	}

	@Test
	void changeUserLateBirthday() {
		User userToUpdate = new User();
		userToUpdate.setEmail("late_birthday@mail.ru");
		userToUpdate.setLogin("Late_Birthday");
		Integer userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId);
		userUpdate.setBirthday(LocalDate.now().plusDays(5));

		assertThrows(ConditionsNotMetException.class, () -> userController.update(userUpdate));
	}

	@Test
	void changeUserLoginWithSpaces() {
		User userToUpdate = new User();
		userToUpdate.setEmail("login_with_spaces@mail.ru");
		userToUpdate.setLogin("Login_With_Spaces");
		Integer userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId);
		userUpdate.setLogin("Currupted User");

		assertThrows(ConditionsNotMetException.class, () -> userController.update(userUpdate));
	}

	@Test
	void changeUserDuplicatedLogin() {
		System.out.println(userController.findAll());
		User ok_user = new User();
		ok_user.setLogin("Ok_User_update");
		ok_user.setEmail("okmail_updated@mail.ru");
		userController.create(ok_user);
		User userToUpdate = new User();
		userToUpdate.setEmail("duplicated_login@mail.ru");
		userToUpdate.setLogin("Duplicated_Login");
		Integer userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId);
		userUpdate.setLogin("Ok_User_update");

		assertThrows(DuplicatedDataException.class, () -> userController.update(userUpdate));
	}

	@Test
	void changeUserWrongEmail() {
		User userToUpdate = new User();
		userToUpdate.setEmail("wrong_mail@mail.ru");
		userToUpdate.setLogin("Wrong_Mail");
		Integer userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId);
		userUpdate.setEmail("mail");

		assertThrows(ConditionsNotMetException.class, () -> userController.update(userUpdate));
	}

	@Test
	void changeUserDuplicatedEmail() {
		System.out.println(userController.findAll());
		User ok_user = new User();
		ok_user.setLogin("OkUser_update");
		ok_user.setEmail("ok_mail_updated@mail.ru");
		userController.create(ok_user);
		User userToUpdate = new User();
		userToUpdate.setEmail("duplicated_mail@mail.ru");
		userToUpdate.setLogin("Duplicated_Mail");
		Integer userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId);
		userUpdate.setEmail("ok_mail_updated@mail.ru");

		assertThrows(DuplicatedDataException.class, () -> userController.update(userUpdate));
	}

	@Test
	void getAllUsers() {
		User user2 = new User();
		user2.setLogin("User");
		user2.setEmail("user2@mail.ru");
		userController.create(user2);

		Collection<User> controllerUserList = userController.findAll();

		assertNotNull(controllerUserList);
		assertEquals(controllerUserList.size(), 8);
	}
}
