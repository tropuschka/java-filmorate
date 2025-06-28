package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component
public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User user);

    User findUserById(Long id);
}
