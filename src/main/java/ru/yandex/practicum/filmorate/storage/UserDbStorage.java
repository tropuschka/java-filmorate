package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.UserDbMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

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
        StringBuilder query = new StringBuilder(("INSERT INTO users (id, email, login, name, birthday) " +
                "VALUES ?, ?, ?, ?, ?;"));
        for (Long friend_id:user.getFriends().keySet()) {
            query.append("INSERT INTO users (user_id, friend_id, confirmed) VALUES ").append(user.getId()).append(", ")
                    .append(friend_id).append(", ").append(user.getFriends().get(friend_id)).append(";");
        }
        jdbc.update(query.toString(),
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        String control = "SELECT * FROM users WHERE = " + user.getId() + ";";
        return jdbc.queryForObject(control, userMapper);
    }

    @Override
    public User update(User user) {
        String query = ("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?;");
        jdbc.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        String control = "SELECT * FROM users WHERE = " + user.getId() + ";";
        return jdbc.queryForObject(control, userMapper);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        String query = ("SELECT * FROM users WHERE id = " + id +";");
        return Optional.ofNullable(jdbc.queryForObject(query, userMapper));
    }
}
