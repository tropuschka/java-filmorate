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
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
	static FilmController filmController = new FilmController(filmStorage);
	static UserController userController = new UserController(userStorage);
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
	static FilmController topFilmController = new FilmController(topFilmStorage);


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
		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> filmController.like(likeFilm.getId(), me.getId()));
		assertEquals("Фильм не найден", notFoundException.getMessage());
		assertEquals(0, likeFilm.getLikes().size());
		assertFalse(likeFilm.getLikes().contains(me.getId()));
	}

	@Test
	void likeFilmDouble() {
		Film likeFilm = new Film();
		likeFilm.setName("Double Liked Film");
		filmController.create(likeFilm);
		filmController.like(likeFilm.getId(), me.getId());
		int likeAmount = likeFilm.getLikes().size();
		DuplicatedDataException duplicatedDataException = assertThrows(DuplicatedDataException.class,
				() -> filmController.like(likeFilm.getId(), me.getId()));
		assertEquals("Лайк уже поставлен", duplicatedDataException.getMessage());
		assertEquals(likeAmount, likeFilm.getLikes().size());
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
		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> filmController.dislike(dislikeNoFilm.getId(), me.getId()));
		assertEquals("Фильм не найден", notFoundException.getMessage());
		assertEquals(0, dislikeNoFilm.getLikes().size());
	}

	@Test
	void dislikeNotLikedFilm() {
		int likeAmount = dislikeFilm.getLikes().size();
		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> filmController.dislike(dislikeFilm.getId(), myFriend.getId()));
		assertEquals("Пользователь с айди " + myFriend.getId() + " не оценивал фильм " + dislikeFilm.getName(),
				conditionsNotMetException.getMessage());
		assertEquals(likeAmount, dislikeFilm.getLikes().size());
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
		FilmController top10FilmController = new FilmController(top10FilmStorage);
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
		FilmController top11FilmController = new FilmController(top11FilmStorage);
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
		FilmController emptyFilmController = new FilmController(emptyFilmStorage);
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
		assertEquals(6, controllerFilmList.size());
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

		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.create(curruptUser));
		assertEquals("Имейл должен быть указан", conditionsNotMetException.getMessage());
	}

	@Test
	void createUserWrongEmail() {
		User curruptUser = new User();
		curruptUser.setLogin("CurruptedUser");
		curruptUser.setEmail("mail");

		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.create(curruptUser));
		assertEquals("Указан некорректный имейл", conditionsNotMetException.getMessage());
	}

	@Test
	void createUserDuplicatedEmail() {
		User okUser = new User();
		okUser.setLogin("OkUser");
		okUser.setEmail("ok_mail@mail.ru");
		userController.create(okUser);
		User curruptUser = new User();
		curruptUser.setLogin("CurruptedUser");
		curruptUser.setEmail("ok_mail@mail.ru");

		DuplicatedDataException duplicatedDataException = assertThrows(DuplicatedDataException.class,
				() -> userController.create(curruptUser));
		assertEquals("Имейл уже используется", duplicatedDataException.getMessage());
	}

	@Test
	void createUserNoLogin() {
		User curruptUser = new User();
		curruptUser.setLogin("");
		curruptUser.setEmail("currupted_user@mail.ru");

		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.create(curruptUser));
		assertEquals("Введите логин", conditionsNotMetException.getMessage());
	}

	@Test
	void createUserLoginWithSpaces() {
		User curruptUser = new User();
		curruptUser.setLogin("Currupted User");
		curruptUser.setEmail("currupted_user@mail.ru");

		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.create(curruptUser));
		assertEquals("В логине не должно быть пробелов", conditionsNotMetException.getMessage());
	}

	@Test
	void createUserDuplicatedLogin() {
		User okUser = new User();
		okUser.setLogin("Ok_User");
		okUser.setEmail("okmail@mail.ru");
		userController.create(okUser);
		User curruptUser = new User();
		curruptUser.setLogin("Ok_User");
		curruptUser.setEmail("currupted_user@mail.ru");

		DuplicatedDataException duplicatedDataException = assertThrows(DuplicatedDataException.class,
				() -> userController.create(curruptUser));
		assertEquals("Логин уже используется", duplicatedDataException.getMessage());
	}

	@Test
	void createUserLateBirthday() {
		User curruptUser = new User();
		curruptUser.setLogin("Currupted_User");
		curruptUser.setEmail("currupted_user@mail.ru");
		curruptUser.setBirthday(LocalDate.now().plusDays(5));

		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.create(curruptUser));
		assertEquals("Указана некорректная дата рождения", conditionsNotMetException.getMessage());
	}

	@Test
	void changeUser() {
		Long userId = userController.create(user).getId();
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

		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.update(userUpdate));
		assertEquals("Id должен быть указан", conditionsNotMetException.getMessage());
	}

	@Test
	void changeUserNotFound() {
		User userToUpdate = new User();
		userToUpdate.setEmail("not_found@mail.ru");
		userToUpdate.setLogin("Not_Found");
		Long userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId + 1);
		userUpdate.setName("New name");

		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> userController.update(userUpdate));
		assertEquals("Пользователь не найден", notFoundException.getMessage());
	}

	@Test
	void changeUserLateBirthday() {
		User userToUpdate = new User();
		userToUpdate.setEmail("late_birthday@mail.ru");
		userToUpdate.setLogin("Late_Birthday");
		Long userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId);
		userUpdate.setBirthday(LocalDate.now().plusDays(5));

		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.update(userUpdate));
		assertEquals("Указана некорректная дата рождения", conditionsNotMetException.getMessage());
	}

	@Test
	void changeUserLoginWithSpaces() {
		User userToUpdate = new User();
		userToUpdate.setEmail("login_with_spaces@mail.ru");
		userToUpdate.setLogin("Login_With_Spaces");
		Long userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId);
		userUpdate.setLogin("Currupted User");

		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.update(userUpdate));
		assertEquals("В логине не должно быть пробелов", conditionsNotMetException.getMessage());
	}

	@Test
	void changeUserDuplicatedLogin() {
		User okUser = new User();
		okUser.setLogin("Ok_User_update");
		okUser.setEmail("okmail_updated@mail.ru");
		userController.create(okUser);
		User userToUpdate = new User();
		userToUpdate.setEmail("duplicated_login@mail.ru");
		userToUpdate.setLogin("Duplicated_Login");
		Long userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId);
		userUpdate.setLogin("Ok_User_update");

		DuplicatedDataException duplicatedDataException = assertThrows(DuplicatedDataException.class,
				() -> userController.update(userUpdate));
		assertEquals("Логин уже используется", duplicatedDataException.getMessage());
	}

	@Test
	void changeUserWrongEmail() {
		User userToUpdate = new User();
		userToUpdate.setEmail("wrong_mail@mail.ru");
		userToUpdate.setLogin("Wrong_Mail");
		Long userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId);
		userUpdate.setEmail("mail");

		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.update(userUpdate));
		assertEquals("Указан некорректный имейл", conditionsNotMetException.getMessage());
	}

	@Test
	void changeUserDuplicatedEmail() {
		System.out.println(userController.findAll());
		User okUser = new User();
		okUser.setLogin("OkUser_update");
		okUser.setEmail("ok_mail_updated@mail.ru");
		userController.create(okUser);
		User userToUpdate = new User();
		userToUpdate.setEmail("duplicated_mail@mail.ru");
		userToUpdate.setLogin("Duplicated_Mail");
		Long userId = userController.create(userToUpdate).getId();
		User userUpdate = new User();
		userUpdate.setId(userId);
		userUpdate.setEmail("ok_mail_updated@mail.ru");

		DuplicatedDataException duplicatedDataException = assertThrows(DuplicatedDataException.class,
				() -> userController.update(userUpdate));
		assertEquals("Имейл уже используется", duplicatedDataException.getMessage());
	}

	@Test
	void addFriend() {
		User friend = new User();
		friend.setLogin("Friend");
		friend.setEmail("friend@mail.ru");
		userController.create(friend);
		assertEquals(3, userController.addFriend(me.getId(), friend.getId()).size());
		assertTrue(me.getFriends().contains(friend.getId()));
		assertTrue(friend.getFriends().contains(me.getId()));
	}

	@Test
	void addFriendToNoUser() {
		User userNoFriends = new User();
		User friend = new User();
		friend.setLogin("Friend_To_Nobody");
		friend.setEmail("friend-to-nobody@mail.ru");
		userController.create(friend);
		userNoFriends.setId((long) (userController.findAll().size() + 1));
		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> userController.addFriend(userNoFriends.getId(), friend.getId()));
		assertEquals("Пользователь с айди " + userNoFriends.getId() + " не существует",
				notFoundException.getMessage());
		assertFalse(friend.getFriends().contains(userNoFriends.getId()));
		assertFalse(userNoFriends.getFriends().contains(friend.getId()));
	}

	@Test
	void addNoFriend() {
		User friend = new User();
		friend.setLogin("No_Friend");
		friend.setEmail("no-friend@mail.ru");
		friend.setId((long) (userController.findAll().size() + 1));
		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> userController.addFriend(me.getId(), friend.getId()));
		assertEquals("Пользователь с айди " + friend.getId() + " не существует",
				notFoundException.getMessage());
		assertFalse(friend.getFriends().contains(me.getId()));
		assertFalse(me.getFriends().contains(friend.getId()));
	}

	@Test
	void addDuplicatedFriend() {
		User friend = new User();
		friend.setLogin("Duplicated_Friend");
		friend.setEmail("duplicated-friend@mail.ru");
		userController.create(friend);
		userController.addFriend(me.getId(), friend.getId());
		int friendAmount = me.getFriends().size();
		DuplicatedDataException duplicatedDataException = assertThrows(DuplicatedDataException.class,
				() -> userController.addFriend(me.getId(), friend.getId()));
		assertEquals("Пользователь " + friend.getName()
				+ " уже есть в списке друзей пользователя " + me.getName(), duplicatedDataException.getMessage());
		assertEquals(friendAmount, me.getFriends().size());
	}

	@Test
	void addSelfFriend() {
		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.addFriend(me.getId(), me.getId()));
		assertEquals("Нельзя добавить в друзья себя", conditionsNotMetException.getMessage());
	}

	@Test
	void deleteFriend() {
		User friend = new User();
		friend.setLogin("Delete_Friend");
		friend.setEmail("delete-friend@mail.ru");
		userController.create(friend);
		me.addFriend(friend);
		friend.addFriend(me);
		int friendAmount = me.getFriends().size();
		friendAmount--;
		assertEquals(friendAmount, userController.deleteFriend(me.getId(), friend.getId()).size());
		assertFalse(me.getFriends().contains(friend.getId()));
		assertFalse(friend.getFriends().contains(me.getId()));
	}

	@Test
	void deleteNotExistingFriend() {
		User friend = new User();
		friend.setLogin("Delete_Not_Existing_Friend");
		friend.setEmail("delete-not-existing-friend@mail.ru");
		friend.setId((long) userController.findAll().size() + 1);
		me.addFriend(friend);
		friend.addFriend(me);
		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> userController.deleteFriend(me.getId(), friend.getId()));
		assertEquals("Пользователь с айди " + friend.getId() + " не существует",
				notFoundException.getMessage());
	}

	@Test
	void deleteNoUserFriend() {
		User friend = new User();
		friend.setLogin("Delete_Not_Existing_Friend");
		friend.setEmail("delete-not-existing-friend@mail.ru");
		friend.setId((long) userController.findAll().size() + 1);
		me.addFriend(friend);
		friend.addFriend(me);
		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> userController.deleteFriend(friend.getId(), me.getId()));
		assertEquals("Пользователь с айди " + friend.getId() + " не существует",
				notFoundException.getMessage());
	}

	/*	@Test
        void deleteNotAFriend() {
            User friend = new User();
            friend.setLogin("Delete_Not_A_Friend");
            friend.setEmail("delete-not-a-friend@mail.ru");
            userController.create(friend);
            NotFoundException notFoundException = assertThrows(NotFoundException.class,
                    () -> userController.deleteFriend(me.getId(), friend.getId()));
            assertEquals("Пользователя " + friend.getName() + " нет в друзьях пользователя " + me.getName(),
                    notFoundException.getMessage());
        }
    */
	@Test
	void deleteSelfFriend() {
		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.deleteFriend(me.getId(), me.getId()));
		assertEquals("Нельзя удалить из друзей себя", conditionsNotMetException.getMessage());
	}

	@Test
	void getFriends() {
		assertEquals(me.getFriends().size(), userController.getFriends(me.getId()).size());
	}

	@Test
	void getFriendsNoUser() {
		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> userController.getFriends((long) userController.findAll().size() + 1));
		assertEquals("Пользователь с айди " + (userController.findAll().size() + 1) + " не существует",
			notFoundException.getMessage());
	}

	@Test
	void findSharedFriends() {
		User otherUser = new User();
		otherUser.setLogin("Other_User");
		otherUser.setEmail("other-user@mail.ru");
		userController.create(otherUser);
		me.addFriend(sharedFriend);
		otherUser.addFriend(othersFriend);
		otherUser.addFriend(sharedFriend);
		assertTrue(userController.findSharedFriends(me.getId(), otherUser.getId()).contains(sharedFriend.getId()));
		assertEquals(1, userController.findSharedFriends(me.getId(), otherUser.getId()).size());
	}

	@Test
	void findNoSharedFriends() {
		User otherUser = new User();
		otherUser.setLogin("User_With_No_Shared_Friends");
		otherUser.setEmail("other-user-with-no-shared@mail.ru");
		userController.create(otherUser);
		otherUser.addFriend(othersFriend);
		assertEquals(0, userController.findSharedFriends(me.getId(), otherUser.getId()).size());
	}

	@Test
	void findSharedFriendsWithNoUser() {
		User otherUser = new User();
		otherUser.setLogin("No_User");
		otherUser.setEmail("no-user@mail.ru");
		otherUser.addFriend(othersFriend);
		otherUser.addFriend(sharedFriend);
		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> userController.findSharedFriends(me.getId(), otherUser.getId()).size());
		assertEquals("Пользователь с айди " + otherUser.getId() + " не существует",
				notFoundException.getMessage());
	}

	@Test
	void findSharedFriendsOfNoUser() {
		User otherUser = new User();
		otherUser.setLogin("No_User");
		otherUser.setEmail("no-user@mail.ru");
		otherUser.addFriend(othersFriend);
		otherUser.addFriend(sharedFriend);
		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> userController.findSharedFriends(otherUser.getId(), me.getId()).size());
		assertEquals("Пользователь с айди " + otherUser.getId() + " не существует",
				notFoundException.getMessage());
	}

	@Test
	void findSelfSharedFriends() {
		ConditionsNotMetException conditionsNotMetException = assertThrows(ConditionsNotMetException.class,
				() -> userController.findSharedFriends(me.getId(), me.getId()).size());
		assertEquals("Для сравнения нужны два разных пользователя", conditionsNotMetException.getMessage());
	}

	@Test
	void getAllUsers() {
		User user2 = new User();
		user2.setLogin("User");
		user2.setEmail("user2@mail.ru");
		userController.create(user2);

		Collection<User> controllerUserList = userController.findAll();

		assertNotNull(controllerUserList);
		assertEquals(userStorage.findAll().size(), controllerUserList.size());
	}
}
