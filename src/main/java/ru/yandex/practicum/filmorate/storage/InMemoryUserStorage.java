package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> userList = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return userList.values();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        userList.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        userList.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findUserById(int id) {
        return Optional.ofNullable(userList.get(id));
    }

    @Override
    public void addFriend(Integer userId, Integer friendId, Boolean confirmed) {
        User user = userList.get(userId);
        User friend = userList.get(friendId);
        user.addFriend(friend);
        if (confirmed == true) {
            user.confirmFriendship(friend);
            friend.confirmFriendship(user);
            update(friend);
        }
        update(user);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId, Boolean confirmed) {
        User user = userList.get(userId);
        User friend = userList.get(friendId);
        user.deleteFriend(friend);
        update(user);
        if (confirmed == true) {
            friend.unconfirmFriendship(user);
            update(friend);
        }
    }

    private int getNextId() {
        int maxId = userList.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
