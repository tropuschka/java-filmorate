package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();;

    public Set<Long> addFriend(User user) {
        friends.add(user.getId());
        return friends;
    }

    public Set<Long> deleteFriend(User user) {
        friends.remove(user.getId());
        return friends;
    }
}
