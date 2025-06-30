package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    final private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<Long> addUserFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + friendId + " не найден"));
        if (user.equals(friend)) {
            throw new ConditionsNotMetException("Нельзя добавить в друзья себя");
        }
        if (user.getFriends() != null && friend.getFriends() != null) {
            if (user.getFriends().contains(friend.getId()) // сделала &&, чтобы в случае, если дружба односторонняя,
                    && friend.getFriends().contains(user.getId())) { // она обновлялась до двусторонней
            throw new DuplicatedDataException("Пользователь " + friend.getName()
                        + " уже есть в списке друзей пользователя " + user.getName());
            }
        }
        user.addFriend(friend);
        friend.addFriend(user);
        log.trace("Пользователь {} добавлен в список друзей пользователя {}", friend.getName(), user.getName());
        return user.getFriends();
    }

    public Collection<Long> deleteUserFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + friendId + " не найден"));
        if (user.equals(friend)) {
            throw new ConditionsNotMetException("Нельзя удалить из друзей себя");
        }
        if (user.getFriends().contains(friend.getId())) user.deleteFriend(friend);
        if (friend.getFriends().contains(user.getId())) friend.deleteFriend(user);
        log.trace("Пользователь {} удален из списка друзей пользователя {}", friend.getName(), user.getName());
        return user.getFriends();
    }

    public Collection<User> getFriends(Long userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        Set<User> friendList = new HashSet<>();
        for (Long friendId : user.getFriends()) {
            if (userStorage.findUserById(friendId).get() != null) {
                friendList.add(userStorage.findUserById(friendId).get());
            }
        }
        return friendList;
    }

    public Collection<User> findSharedFriend(Long userId, Long otherId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + userId + " не найден"));
        User friend = userStorage.findUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с айди " + otherId + " не найден"));
        if (user.equals(friend)) {
            throw  new ConditionsNotMetException("Для сравнения нужны два разных пользователя");
        }
        Set<Long> sharedFriendsId = user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .collect(Collectors.toSet());
        Set<User> sharedFriends = new HashSet<>();
        for (Long friendId : sharedFriendsId) {
            if (userStorage.findUserById(friendId).get() != null) {
                sharedFriends.add(userStorage.findUserById(friendId).get());
            }
        }
        log.trace("У пользователя {} и пользователя {} {} общих друзей",
                user.getName(), friend.getName(), sharedFriendsId.size());
        return sharedFriends;
    }
}
