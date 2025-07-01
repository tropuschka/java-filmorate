package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTests extends FilmorateApplicationTests {
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
}
