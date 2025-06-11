package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> userList = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return userList.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throwValidationException("Имейл должен быть указан");
        }
        if (!(user.getEmail().contains("@"))) {
            throwValidationException("Указан некорректный имейл");
        }
        if (duplicateData(user.getEmail())) {
            throwDuplicationException("Имейл уже используется");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throwValidationException("Введите логин");
        }
        if (user.getLogin().contains(" ")) {
            throwValidationException("В логине не должно быть пробелов");
        }
        if (duplicateData(user.getLogin())) {
            throwDuplicationException("Логин уже используется");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throwValidationException("Указана некорректная дата рождения");
        }

        user.setId(getNextId());
        userList.put(user.getId(), user);
        log.trace("Создан пользователь {}, айди {}", user.getLogin(), user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            throwValidationException("Id должен быть указан");
        }
        User oldUser = userList.get(user.getId());
        if (oldUser == null) {
            throwNotFoundException("Пользователь не найден");
        }
        User newUser = new User();
        newUser.setId(user.getId());

        if (user.getName() != null && !user.getName().isBlank()) {
            newUser.setName(user.getName());
        } else {
            newUser.setName(oldUser.getName());
        }
        if (user.getBirthday() != null) {
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throwValidationException("Указана некорректная дата рождения");
            }
            newUser.setBirthday(user.getBirthday());
        } else {
            newUser.setBirthday(oldUser.getBirthday());
        }
        if (user.getLogin() != null && !user.getLogin().isBlank()) {
            if (user.getLogin().contains(" ")) {
                throwValidationException("В логине не должно быть пробелов");
            }
            if (duplicateData(user.getLogin()) && !(oldUser.getLogin().equals(user.getLogin()))) {
                throwDuplicationException("Логин уже используется");
            }

            newUser.setLogin(user.getLogin());
        } else {
            newUser.setLogin(oldUser.getLogin());
        }
        if (user.getEmail() != null) {
            if (!(user.getEmail().contains("@"))) {
                throwValidationException("Указан некорректный имейл");
            }
            if (duplicateData(user.getEmail()) && !(oldUser.getEmail().equals(user.getEmail()))) {
                throwDuplicationException("Имейл уже используется");
            }
            newUser.setEmail(user.getEmail());
        } else {
            newUser.setEmail(oldUser.getEmail());
        }

        userList.put(user.getId(), newUser);
        log.trace("Данные пользователя {}, айди {}, обновлены", newUser.getLogin(), newUser.getId());
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

    private void throwValidationException(String message) {
        log.error(message);
        throw new ConditionsNotMetException(message);
    }

    private void throwDuplicationException(String message) {
        log.error(message);
        throw new DuplicatedDataException(message);
    }

    private void throwNotFoundException(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }
}
