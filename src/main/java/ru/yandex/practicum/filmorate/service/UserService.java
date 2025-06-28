package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    public static Collection<Long> addUserFriend(UserStorage userStorage, Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
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

    public static Collection<Long> deleteUserFriend(UserStorage userStorage, Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        if (user.equals(friend)) {
            throw new ConditionsNotMetException("Нельзя удалить из друзей себя");
        }
        if (user.getFriends().contains(friend.getId())) user.deleteFriend(friend);
        if (friend.getFriends().contains(user.getId())) friend.deleteFriend(user);
        log.trace("Пользователь {} удален из списка друзей пользователя {}", friend.getName(), user.getName());
        return user.getFriends();
    }

    public static Collection<User> getFriends(UserStorage userStorage, Long userId) {
        User user = userStorage.findUserById(userId);
        Set<User> friendList = new HashSet<>();
        for (Long friendId : user.getFriends()) {
            friendList.add(userStorage.findUserById(friendId));
        }
        return friendList;
    }

    public static Collection<User> findSharedFriend(UserStorage userStorage, Long userId, Long otherId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(otherId);
        if (user.equals(friend)) {
            throw  new ConditionsNotMetException("Для сравнения нужны два разных пользователя");
        }
        Set<Long> sharedFriendsId = user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .collect(Collectors.toSet());
        Set<User> sharedFriends = new HashSet<>();
        for (Long friendId : sharedFriendsId) {
            sharedFriends.add(userStorage.findUserById(friendId));
        }
        log.trace("У пользователя {} и пользователя {} {} общих друзей",
                user.getName(), friend.getName(), sharedFriendsId.size());
        return sharedFriends;
    }
}
