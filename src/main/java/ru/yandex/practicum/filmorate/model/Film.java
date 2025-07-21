package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Integer> genres;
    private Integer age_rating_id;
    private Set<Long> likes = new HashSet<>();

    public void like(Long userId) {
        likes.add(userId);
    }

    public void dislike(Long userId) {
        likes.remove(userId);
    }

    public Integer likeAmount() {
        return likes.size();
    }
}
