package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    public static Set<Long> addUserFriend(UserStorage userStorage, User user, User friend) {
        if (userStorage.findUserById(user.getId()).isEmpty()) {
            ExceptionService.throwNotFoundException("Пользователь " + user.getName() + " не существует");
        }
        if (userStorage.findUserById(friend.getId()).isEmpty()) {
            ExceptionService.throwNotFoundException("Пользователь " + friend.getName() + " не существует");
        }
        if (user.equals(friend)) {
            ExceptionService.throwValidationException("Нельзя добавить в друзья себя");
        }
        if (user.getFriends() != null && friend.getFriends() != null) {
            if (user.getFriends().contains(friend.getId()) // сделала &&, чтобы в случае, если дружба односторонняя,
                    && friend.getFriends().contains(user.getId())) { // она обновлялась до двусторонней
            ExceptionService.throwDuplicationException("Пользователь " + friend.getName()
                        + " уже есть в списке друзей пользователя " + user.getName());
            }
        }
        user.addFriend(friend);
        friend.addFriend(user);
        log.trace("Пользователь {} добавлен в список друзей пользователя {}", friend.getName(), user.getName());
        return user.getFriends();
    }

    public static Set<Long> deleteUserFriend(UserStorage userStorage, User user, User friend) {
        if (userStorage.findUserById(user.getId()).isEmpty()) {
            ExceptionService.throwNotFoundException("Пользователь " + user.getName() + " не существует");
        }
        if (userStorage.findUserById(friend.getId()).isEmpty()) {
            ExceptionService.throwNotFoundException("Пользователь " + friend.getName() + " не существует");
        }
        if (user.equals(friend)) {
            ExceptionService.throwValidationException("Нельзя удалить из друзей себя");
        }
        if (!(user.getFriends().contains(friend.getId()) && friend.getFriends().contains(user.getId()))) {
            ExceptionService.throwNotFoundException("Пользователя " + friend.getName() + " нет в друзьях пользователя " + user.getName());
        }
        if (user.getFriends().contains(friend.getId())) user.deleteFriend(friend);
        if (friend.getFriends().contains(user.getId())) friend.deleteFriend(user);
        log.trace("Пользователь {} удален из списка друзей пользователя {}", friend.getName(), user.getName());
        return user.getFriends();
    }

    public static Set<Long> findSharedFriend(UserStorage userStorage, User user, User friend) {
        if (userStorage.findUserById(user.getId()).isEmpty()) {
            ExceptionService.throwNotFoundException("Пользователь " + user.getName() + " не существует");
        }
        if (userStorage.findUserById(friend.getId()).isEmpty()) {
            ExceptionService.throwNotFoundException("Пользователь " + friend.getName() + " не существует");
        }
        if (user.equals(friend)) {
            ExceptionService.throwValidationException("Для сравнения нужны два разных пользователя");
        }
        Set<Long> sharedFriends = user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .collect(Collectors.toSet());
        return sharedFriends;
    }
}
