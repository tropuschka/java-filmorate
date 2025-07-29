package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Component
public interface GenreStorage {
    Collection<Genre> findAll();

    Optional<Genre> findGenreById(int id);
}
