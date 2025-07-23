package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Integer> genres = new HashSet<>();
    private Integer mpa;
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
