package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

public class UserController {
    HashMap<Integer, User> userList = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return userList.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (!(user.getEmail().contains("@"))) {
            throw new ConditionsNotMetException("Указан некорректный имейл");
        }
        if (duplicateData(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ConditionsNotMetException("Введите логин");
        }
        if (user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("В логине не должно быть пробелов");
        }
        if (duplicateData(user.getLogin())) {
            throw new DuplicatedDataException("Этот логин уже используется");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Указана некорректная дата рождения");
        }

        user.setId(getNextId());
        userList.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User oldUser = userList.get(user.getId());
        User newUser = null;
        newUser.setId(user.getId());

        if (user.getName() != null) {
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
        if (user.getLogin() != null) {
            if (user.getLogin().contains(" ")) {
                throw new ConditionsNotMetException("В логине не должно быть пробелов");
            }
            if (duplicateData(user.getLogin()) && !(oldUser.getLogin().equals(user.getLogin()))) {
                throw new DuplicatedDataException("Этот логин уже используется");
            }

            newUser.setLogin(user.getLogin());
        } else {
            newUser.setLogin(oldUser.getLogin());
        }
        if (user.getEmail() != null) {
            if (!(user.getEmail().contains("@"))) {
                throw new ConditionsNotMetException("Указан некорректный имейл");
            }
            if (duplicateData(user.getEmail()) && !(oldUser.getEmail().equals(user.getEmail()))) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            newUser.setEmail(user.getEmail());
        } else {
            newUser.setEmail(oldUser.getEmail());
        }

        userList.remove(user.getId());
        userList.put(user.getId(), newUser);
        return newUser;
    }

    private Integer getNextId() {
        int maxId = userList.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }

    private boolean duplicateData(String data) {
        return userList.values().stream().map(User::getEmail).anyMatch(m -> m.equals(data));
    }
}
