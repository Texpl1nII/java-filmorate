package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {
    @Test
    void shouldFailWhenNameIsEmpty() {
        Film film = new Film();
        film.setName("");
        assertThrows(ValidationException.class, film::validate, "Film name cannot be empty");
    }

    @Test
    void shouldFailWhenDescriptionExceeds200() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, film::validate, "Description must not exceed 200 characters");
    }

    @Test
    void shouldFailWhenReleaseDateBefore1895() {
        Film film = new Film();
        film.setName("Test Film");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, film::validate, "Release date must be on or after December 28, 1895");
    }

    @Test
    void shouldFailWhenDurationNotPositive() {
        Film film = new Film();
        film.setName("Test Film");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);
        assertThrows(ValidationException.class, film::validate, "Duration must be positive");
    }

    @Test
    void shouldPassWhenValidFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        assertDoesNotThrow(film::validate);
    }
}