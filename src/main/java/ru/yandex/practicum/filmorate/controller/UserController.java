package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

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

    @GetMapping("/{userId}/friend/shared")
    public Set<Long> findSharedFriends(@RequestBody User user, @PathVariable Long userId) {
        return UserService.findSharedFriend(userStorage, user, userId);
    }

    @PostMapping("/{userId}/friend")
    public Set<Long> addFriend(@RequestBody User user, @PathVariable Long userId) {
        return UserService.addUserFriend(userStorage, user, userId);
    }

    @DeleteMapping("/{userId}/friend")
    public Set<Long> deleteFriend(@RequestBody User user, @PathVariable Long userId) {
        return UserService.deleteUserFriend(userStorage, user, userId);
    }
}
