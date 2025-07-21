package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Map<Long, Boolean> friends = new HashMap<>();

    public Set<Long> addFriend(User user) {
        friends.put(user.getId(), false);
        return friends.keySet();
    }

    public Set<Long> deleteFriend(User user) {
        friends.remove(user.getId());
        return friends.keySet();
    }
}
