package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.UserDbMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements  UserStorage {
    private final JdbcTemplate jdbc;
    private final UserDbMapper userMapper;

    @Override
    public Collection<User> findAll() {
        String query = "SELECT * FROM users";
        return jdbc.query(query, userMapper);
    }
}
