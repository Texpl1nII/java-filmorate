package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDate;

import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не может быть более 200 символов")
    private String description;

    @FilmReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    @NotNull(message = "Рейтинг MPA не может быть пустым")
    private MpaRating mpa;

    private List<Genre> genres = new ArrayList<>();
    private Set<Long> likes = new HashSet<>();
}