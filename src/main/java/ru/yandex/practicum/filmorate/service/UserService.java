package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Set;

@Slf4j
@Service
public class UserService {
    public Set<Long> addFriend(UserStorage userStorage, User user, User friend) {
        if (userStorage.findUserById(user.getId()).isEmpty()
                || userStorage.findUserById(friend.getId()).isEmpty()) {
            throwNotFoundException("Пользователь не существует");
        }
        if (user.equals(friend)) {
            throwValidationException("Нельзя добавить в друзья себя");
        }
        if (user.getFriends() != null && friend.getFriends() != null) {
            if (user.getFriends().contains(friend.getId()) // сделала &&, чтобы в случае, если дружба односторонняя,
                    && friend.getFriends().contains(user.getId())) { // она обновлялась до двусторонней
                throwDuplicationException("Пользователь уже добавлен");
            }
        }
        user.addUserFriend(friend);
        friend.addUserFriend(user);
        log.trace("Пользователь {} добавлен в список друзей пользователя {}", friend.getName(), user.getName());
        return user.getFriends();
    }

    private void throwValidationException(String message) {
        log.error(message);
        throw new ConditionsNotMetException(message);
    }

    private void throwNotFoundException(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }

    private void throwDuplicationException(String message) {
        log.error(message);
        throw new DuplicatedDataException(message);
    }
}
