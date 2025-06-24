package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ExceptionService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> userList = new HashMap<>();
    private static final ExceptionService exceptionService = new ExceptionService();

    @Override
    public Collection<User> findAll() {
        return userList.values();
    }

    @Override
    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (!(user.getEmail().contains("@"))) {
            throw new ConditionsNotMetException("Указан некорректный имейл");
        }
        if (duplicateMail(user.getEmail())) {
            ExceptionService.throwDuplicationException("Имейл уже используется");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ConditionsNotMetException("Введите логин");
        }
        if (user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("В логине не должно быть пробелов");
        }
        if (duplicateLogin(user.getLogin())) {
            ExceptionService.throwDuplicationException("Логин уже используется");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Указана некорректная дата рождения");
        }

        user.setId(getNextId());
        userList.put(user.getId(), user);
        log.trace("Создан пользователь {}, айди {}", user.getLogin(), user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User oldUser = userList.get(user.getId());
        if (oldUser == null) {
            ExceptionService.throwNotFoundException("Пользователь не найден");
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
                ExceptionService.throwDuplicationException("Логин уже используется");
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
                ExceptionService.throwDuplicationException("Имейл уже используется");
            }
            newUser.setEmail(user.getEmail());
        } else {
            newUser.setEmail(oldUser.getEmail());
        }

        userList.put(user.getId(), newUser);
        log.trace("Данные пользователя {}, айди {}, обновлены", newUser.getLogin(), newUser.getId());
        return newUser;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        Optional<User> user = Optional.ofNullable(userList.get(id));
        return user;
    }

    private Long getNextId() {
        long maxId = userList.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }

    private boolean duplicateMail(String data) {
        return userList.values().stream().map(User::getEmail).anyMatch(m -> m.equals(data));
    }

    private boolean duplicateLogin(String data) {
        return userList.values().stream().map(User::getLogin).anyMatch(m -> m.equals(data));
    }
}
