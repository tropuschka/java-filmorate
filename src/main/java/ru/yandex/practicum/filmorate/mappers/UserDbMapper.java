package ru.yandex.practicum.filmorate.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component("UserDbMapper")
@RequiredArgsConstructor
public class UserDbMapper implements RowMapper<User> {
    private final JdbcTemplate jdbc;

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));

        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
        user.setBirthday(birthday);

        String friendsSql = "SELECT friend_id, confirmed FROM friends WHERE user_id=" + user.getId();
        ResultSetExtractor friendsExtractor = new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<Long, Boolean> friendList = new HashMap<>();
                while(rs.next()) {
                    Long friend_id = rs.getLong("friend_id");
                    Boolean status = rs.getBoolean("confirmed");
                    friendList.put(friend_id, status);
                }
                return friendList;
            }
        };
        user.setFriends((HashMap) jdbc.query(friendsSql, friendsExtractor));

        return user;
    }
}
