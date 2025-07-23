package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> filmList = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return filmList.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        filmList.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        filmList.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        return Optional.ofNullable(filmList.get(id));
    }

    @Override
    public Collection<Film> getTop(int amount) {
        ArrayList<Film> filmTop = filmList.values().stream()
                .sorted(Comparator.comparing(Film::likeAmount).reversed())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
        List<Film> top10 = new ArrayList<>();
        if (!filmTop.isEmpty()) {
            for (int i = 0; i < amount && i < filmTop.size(); i++) {
                top10.add(filmTop.get(i));
            }
        }
        return top10;
    }

    private int getNextId() {
        int maxId = filmList.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
