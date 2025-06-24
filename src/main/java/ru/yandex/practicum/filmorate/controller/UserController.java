package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ExceptionService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return userStorage.update(user);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        return UserService.getFriends(userStorage, id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findSharedFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return UserService.findSharedFriend(userStorage, id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Collection<Long> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return UserService.addUserFriend(userStorage, id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Collection<Long> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return UserService.deleteUserFriend(userStorage, id, friendId);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ConditionsNotMetException.class)
    public void conditionsNotMet(ConditionsNotMetException e) {
        ExceptionService.throwValidationException(e.getMessage());
    }
}
