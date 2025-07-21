package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FriendDbMapper implements RowMapper<Map<Long, Boolean>> {
    @Override
    public Map<Long, Boolean> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Map<Long, Boolean> friends = new HashMap<>();
        Long friend_id = resultSet.getLong("friend_id");
        boolean friendship_status = resultSet.getBoolean("confirmed");
        friends.put(friend_id, friendship_status);
        return friends;
    }
}
