package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

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
        assertTrue(userController.findSharedFriends(me.getId(), otherUser.getId()).contains(sharedFriend));
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
