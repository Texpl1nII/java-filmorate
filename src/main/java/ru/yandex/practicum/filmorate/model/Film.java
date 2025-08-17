package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    private Set<Genre> genres = new HashSet<>();
    private Set<Long> likes = new HashSet<>();

    public List<Genre> getGenres() {
        if (genres == null) {
            return new ArrayList<>();
        }
        return genres.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    public void setGenres(Collection<Genre> genres) {
        if (genres == null) {
            this.genres = new HashSet<>();
        } else {
            this.genres = new HashSet<>(genres);
        }
    }
}