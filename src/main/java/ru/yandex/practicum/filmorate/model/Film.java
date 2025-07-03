package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

@Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Film name cannot be empty");
        }
        if (description != null && description.length() > 200) {
            throw new ValidationException("Description must not exceed 200 characters");
        }
        if (releaseDate != null && releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Release date must be on or after December 28, 1895");
        }
        if (duration != null && duration <= 0) {
            throw new ValidationException("Duration must be positive");
        }
    }
}
