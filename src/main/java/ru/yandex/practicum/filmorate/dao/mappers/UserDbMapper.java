package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component("UserDbMapper")
public class UserDbMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));

        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
        user.setBirthday(birthday);

        return user;
    }
}
