package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.UserDbMapper;

@Repository("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements  UserStorage {
    private final JdbcTemplate jdbc;
    private final UserDbMapper userMapper;
}
