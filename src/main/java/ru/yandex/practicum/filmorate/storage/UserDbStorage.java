package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.UserDbMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements  UserStorage {
    private final JdbcTemplate jdbc;
    private final UserDbMapper userMapper;

    @Override
    public Collection<User> findAll() {
        String query = "SELECT * FROM users;";
        return jdbc.query(query, userMapper);
    }

    @Override
    public User create(User user) {
        String query = ("INSERT INTO users (id, email, login, name, birthday) VALUES ?, ?, ?, ?, ?;");
        for (Long friend_id:user.getFriends().keySet()) {
            query = query + "INSERT INTO users (user_id, friend_id, confirmed) VALUES " + user.getId() + ", " +
                    friend_id + ", " + user.getFriends().get(friend_id) + ";";
        }
        jdbc.update(query);
        String control = "SELECT * FROM users WHERE id=" + user.getId();
        return jdbc.queryForObject(control, userMapper);
    }
}
