package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.UserDbMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements  UserStorage {
    @Autowired
    private final JdbcTemplate jdbc;
    @Autowired
    private final UserDbMapper userMapper;

    @Override
    public Collection<User> findAll() {
        String query = "SELECT * FROM users;";
        return jdbc.query(query, userMapper);
    }

    @Override
    public User create(User user) {
        String query = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?);";
        jdbc.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        Integer userDbId = jdbc.queryForObject("SELECT id FROM users WHERE email = ?;",
                Integer.class, user.getEmail());

        String control = "SELECT * FROM users WHERE id = ?;";
        return jdbc.queryForObject(control, userMapper, userDbId);
    }

    @Override
    public User update(User user) {
        String query = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?;";
        jdbc.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        String control = "SELECT * FROM users WHERE id = ?;";
        return jdbc.queryForObject(control, userMapper, user.getId());
    }

    @Override
    public Optional<User> findUserById(int id) {
        String query = ("SELECT * FROM users WHERE id = ?;");
        List<User> users = jdbc.query(query, userMapper, id);
        if (users.isEmpty()) return Optional.empty();
        return Optional.ofNullable(users.getFirst());
    }

    @Override
    public void addFriend(Integer userId, Integer friendId, Boolean confirmed) {
        String friendQuery = "INSERT INTO friends (user_id, friend_id, confirmed) VALUES ( ?, ?, ?);";
        jdbc.update(friendQuery, userId, friendId, confirmed);
        if (confirmed == true) {
            String unconfirmQuery = "UPDATE friends SET confirmed = true WHERE user_id = ? AND friend_id = ?;";
            jdbc.update(unconfirmQuery, friendId, userId);
        }
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId, Boolean confirmed) {
        String deleteQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?;";
        jdbc.update(deleteQuery, userId, friendId);
        if (confirmed == true) {
            String unconfirmQuery = "UPDATE friends SET confirmed = false WHERE user_id = ? AND friend_id = ?;";
            jdbc.update(unconfirmQuery, friendId, userId);
        }
    }

}
