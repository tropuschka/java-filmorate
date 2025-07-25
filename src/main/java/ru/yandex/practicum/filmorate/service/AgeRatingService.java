package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.AgeRating;
import ru.yandex.practicum.filmorate.storage.AgeRatingStorage;

import java.util.Collection;

@Slf4j
@Component
public class AgeRatingService {
    private final AgeRatingStorage ageRatingStorage;

    @Autowired
    public AgeRatingService(@Qualifier("AgeRatingDbStorage") AgeRatingStorage ageRatingStorage) {
        this.ageRatingStorage = ageRatingStorage;
    }

    public Collection<AgeRating> findAll() {
        return ageRatingStorage.findAll();
    }

    public AgeRating findById(int id) {
        return ageRatingStorage.findAgeRatingById(id)
                .orElseThrow(() -> new NotFoundException("Возрастной рейтинг не найден"));
    }
}
