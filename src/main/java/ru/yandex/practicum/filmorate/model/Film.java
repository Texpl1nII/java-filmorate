package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    @NotBlank(message = "Film name cannot be empty")
    private String name;
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
    @NotNull(message = "Release date must not be null")
    @PastOrPresent(message = "Release date cannot be in the future")
    private LocalDate releaseDate;
    @NotNull(message = "Duration must not be null")
    @Positive(message = "Duration must be positive")
    private Integer duration;
    private Set<Long> likes = new HashSet<>();
    private Set<Integer> genreIds = new HashSet<>();
    private Integer mpaRatingId;
}