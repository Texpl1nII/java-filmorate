package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenReleaseDateBefore1895() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // Before 28 Dec 1895
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation should fail for release date before 28 Dec 1895");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Дата выпуска не может быть раньше 28 декабря 1895 года")),
                "Violation should mention release date constraint");
    }

    @Test
    void shouldPassWhenReleaseDateOnOrAfter1895() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28)); // On 28 Dec 1895
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Validation should pass for release date on or after 28 Dec 1895");
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation should fail for blank name");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Название фильма не может быть пустым")),
                "Violation should mention name constraint");
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(201)); // Exceeds 200 characters
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation should fail for description longer than 200 characters");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Описание не должно превышать 200 символов")),
                "Violation should mention description length constraint");
    }

    @Test
    void shouldFailWhenDurationNotPositive() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation should fail for non-positive duration");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Продолжительность фильма должна быть положительной")),
                "Violation should mention duration constraint");
    }
}