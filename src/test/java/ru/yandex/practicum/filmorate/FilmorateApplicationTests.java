package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;

@SpringBootTest
class FilmorateApplicationTests {
	static Film film = new Film();
	static Film dislikeFilm = new Film();
	static User user = new User();
	static User me = new User();
	static User myFriend = new User();
	static User othersFriend = new User();
	static User sharedFriend = new User();
	static FilmStorage filmStorage = new InMemoryFilmStorage();
	static UserStorage userStorage = new InMemoryUserStorage();
	static UserController userController = new UserController(userStorage);
	static FilmController filmController = new FilmController(filmStorage, userController);
	static Film topFilm1 = new Film();
	static Film topFilm2 = new Film();
	static Film topFilm3 = new Film();
	static Film topFilm4 = new Film();
	static Film topFilm5 = new Film();
	static Film topFilm6 = new Film();
	static Film topFilm7 = new Film();
	static Film topFilm8 = new Film();
	static Film topFilm9 = new Film();
	static FilmStorage topFilmStorage = new InMemoryFilmStorage();
	static FilmController topFilmController = new FilmController(topFilmStorage, userController);

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

		me.setLogin("Me");
		me.setEmail("my@mail.ru");
		userController.create(me);

		myFriend.setLogin("My_Friend");
		myFriend.setEmail("my-friend@mail.ru");
		userController.create(myFriend);
		userController.addFriend(me.getId(), myFriend.getId());

		othersFriend.setLogin("Others_Friend");
		othersFriend.setEmail("others-friend@mail.ru");
		userController.create(othersFriend);

		sharedFriend.setLogin("Shared_Friend");
		sharedFriend.setEmail("shared-friend@mail.ru");
		userController.create(sharedFriend);
		me.addFriend(sharedFriend);
	}
}
