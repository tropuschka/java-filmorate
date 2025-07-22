package ru.yandex.practicum.filmorate.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.AgeRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("AgeRatingMapper")
@RequiredArgsConstructor
public class AgeRatingMapper implements RowMapper<AgeRating> {
    @Override
    public AgeRating mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        AgeRating ageRating = new AgeRating();
        ageRating.setId(resultSet.getInt("id"));
        ageRating.setName(resultSet.getString("name"));
        ageRating.setDescription(resultSet.getString("description"));
        return  ageRating;
    }
}
