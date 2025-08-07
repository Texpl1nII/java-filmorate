package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.AfterDate;

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
    @AfterDate(value = "1895-12-28", message = "Release date must be on or after December 28, 1895")
    private LocalDate releaseDate;
    @NotNull(message = "Duration must not be null")
    @Positive(message = "Duration must be positive")
    private Integer duration;
    private Set<Long> likes = new HashSet<>();
    private Set<Integer> genreIds = new HashSet<>();
    private Integer mpaRatingId;
}