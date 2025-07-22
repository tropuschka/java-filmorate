package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(int id) {
        return userStorage.findUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (!(user.getEmail().contains("@"))) {
            throw new ConditionsNotMetException("Указан некорректный имейл");
        }
        if (duplicateMail(user.getEmail())) {
            throw new DuplicatedDataException("Имейл уже используется");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ConditionsNotMetException("Введите логин");
        }
        if (user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("В логине не должно быть пробелов");
        }
        if (duplicateLogin(user.getLogin())) {
            throw new DuplicatedDataException("Логин уже используется");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Указана некорректная дата рождения");
        }

        User newUser = userStorage.create(user);
        log.trace("Создан пользователь {}, айди {}", newUser.getLogin(), newUser.getId());
        return newUser;
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User oldUser = userStorage.findUserById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        User newUser = new User();
        newUser.setId(user.getId());

        if (user.getName() != null && !user.getName().isBlank()) {
            newUser.setName(user.getName());
        } else {
            newUser.setName(oldUser.getName());
        }
        if (user.getBirthday() != null) {
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new ConditionsNotMetException("Указана некорректная дата рождения");
            }
            newUser.setBirthday(user.getBirthday());
        } else {
            newUser.setBirthday(oldUser.getBirthday());
        }
        if (user.getLogin() != null && !user.getLogin().isBlank()) {
            if (user.getLogin().contains(" ")) {
                throw new ConditionsNotMetException("В логине не должно быть пробелов");
            }
            if (duplicateLogin(user.getLogin()) && !(oldUser.getLogin().equals(user.getLogin()))) {
                throw new DuplicatedDataException("Логин уже используется");
            }

            newUser.setLogin(user.getLogin());
        } else {
            newUser.setLogin(oldUser.getLogin());
        }
        if (user.getEmail() != null) {
            if (!(user.getEmail().contains("@"))) {
                throw new ConditionsNotMetException("Указан некорректный имейл");
            }
            if (duplicateMail(user.getEmail()) && !(oldUser.getEmail().equals(user.getEmail()))) {
                throw new DuplicatedDataException("Имейл уже используется");
            }
            newUser.setEmail(user.getEmail());
        } else {
            newUser.setEmail(oldUser.getEmail());
        }
        if (user.getFriends() != null) {
            newUser.setFriends(user.getFriends());
        } else {
            newUser.setFriends(oldUser.getFriends());
        }
        User updateUser = userStorage.update(newUser);
        log.trace("Данные пользователя {}, айди {}, обновлены", updateUser.getLogin(), updateUser.getId());
        return updateUser;
    }

    public Collection<Integer> addUserFriend(int userId, int friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + friendId + " не найден"));
        if (user.equals(friend)) {
            throw new ConditionsNotMetException("Нельзя добавить в друзья себя");
        }
        if (user.getFriends() != null && friend.getFriends() != null) {
            if (user.getFriends().keySet().contains(friend.getId())) {
            throw new DuplicatedDataException("Пользователь " + friend.getName()
                        + " уже есть в списке друзей пользователя " + user.getName());
            }
        }
        user.addFriend(friend);
        if (friend.getFriends().keySet().contains(user.getId())) {
            user.confirmFriendship(friend);
            friend.confirmFriendship(user);
            update(friend);
        }
        update(user);
        log.trace("Пользователь {} добавлен в список друзей пользователя {}", friend.getName(), user.getName());
        return user.getFriends().keySet();
    }

    public Collection<Integer> deleteUserFriend(int userId, int friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + friendId + " не найден"));
        if (user.equals(friend)) {
            throw new ConditionsNotMetException("Нельзя удалить из друзей себя");
        }
        if (user.getFriends().keySet().contains(friend.getId())) {
            user.deleteFriend(friend);
            update(user);
        }
        if (friend.getFriends().keySet().contains(user.getId())) {
            friend.unconfirmFriendship(user);
            update(friend);
        }
        log.trace("Пользователь {} удален из списка друзей пользователя {}", friend.getName(), user.getName());
        return user.getFriends().keySet();
    }

    public Collection<User> getFriends(int userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        Set<User> friendList = new HashSet<>();
        for (int friendId : user.getFriends().keySet()) {
            if (userStorage.findUserById(friendId).get() != null) {
                friendList.add(userStorage.findUserById(friendId).get());
            }
        }
        return friendList;
    }

    public Collection<User> findSharedFriend(int userId, int otherId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        User friend = userStorage.findUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + otherId + " не найден"));
        if (user.equals(friend)) {
            throw  new ConditionsNotMetException("Для сравнения нужны два разных пользователя");
        }
        Set<Integer> sharedFriendsId = user.getFriends().keySet().stream()
                .filter(friend.getFriends().keySet()::contains)
                .collect(Collectors.toSet());
        Set<User> sharedFriends = new HashSet<>();
        for (int friendId : sharedFriendsId) {
            if (userStorage.findUserById(friendId).get() != null) {
                sharedFriends.add(userStorage.findUserById(friendId).get());
            }
        }
        log.trace("У пользователя {} и пользователя {} {} общих друзей",
                user.getName(), friend.getName(), sharedFriendsId.size());
        return sharedFriends;
    }

    private boolean duplicateMail(String data) {
        return userStorage.findAll().stream().map(User::getEmail).anyMatch(m -> m.equals(data));
    }

    private boolean duplicateLogin(String data) {
        return userStorage.findAll().stream().map(User::getLogin).anyMatch(m -> m.equals(data));
    }
}
