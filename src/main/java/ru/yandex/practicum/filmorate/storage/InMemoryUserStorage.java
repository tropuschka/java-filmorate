package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> userList = new HashMap<>();

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
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(userList.get(id));
    }

    private Long getNextId() {
        long maxId = userList.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
