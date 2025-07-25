package ru.yandex.practicum.filmorate.storage;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Component
public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User user);

    Optional<User> findUserById(int id);

    void addFriend(Integer userId, Integer friendId, Boolean confirmed);

    void removeFriend(Integer userId, Integer friendId, Boolean confirmed);
}
