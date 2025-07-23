package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
    private AgeRating mpa;
    private Set<Integer> likes = new HashSet<>();

    public void like(int userId) {
        likes.add(userId);
    }

    public void dislike(int userId) {
        likes.remove(userId);
    }

    public Integer likeAmount() {
        return likes.size();
    }
}
