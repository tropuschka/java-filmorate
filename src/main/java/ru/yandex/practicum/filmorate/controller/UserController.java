package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return userStorage.findUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
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
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findSharedFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.findSharedFriend(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Collection<Long> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.addUserFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Collection<Long> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.deleteUserFriend(id, friendId);
    }
}
