package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Map<Integer, Boolean> friends = new HashMap<>();

    public Set<Integer> addFriend(User user) {
        friends.put(user.getId(), false);
        return friends.keySet();
    }

    public Set<Integer> deleteFriend(User user) {
        friends.remove(user.getId());
        return friends.keySet();
    }

    public Set<Integer> confirmFriendship(User user) {
        friends.put(user.getId(), true);
        return friends.keySet();
    }

    public Set<Integer> unconfirmFriendship(User user) {
        friends.put(user.getId(), false);
        return friends.keySet();
    }
}
