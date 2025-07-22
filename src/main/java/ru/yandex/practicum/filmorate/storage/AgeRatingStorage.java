package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.AgeRating;

import java.util.Collection;
import java.util.Optional;

@Component
public interface AgeRatingStorage {
    Collection<AgeRating> findAll();

    Optional<AgeRating> findAgeRatingById(int id);
}
