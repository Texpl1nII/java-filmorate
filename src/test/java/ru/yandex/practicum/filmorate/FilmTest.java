package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        Film film = new Film();
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Film name cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenDescriptionExceeds200() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("a".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Description must not exceed 200 characters", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenReleaseDateBefore1895() {
        Film film = new Film();
        film.setName("Test Film");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Release date must be on or after December 28, 1895", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenDurationNotPositive() {
        Film film = new Film();
        film.setName("Test Film");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Duration must be positive", violations.iterator().next().getMessage());
    }

    @Test
    void shouldPassWhenValidFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenFieldsAreNull() {
        Film film = new Film();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Film name cannot be empty")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Duration must not be null")));
    }

    @Test
    void shouldPassWhenReleaseDateIs18951228() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassWhenDescriptionIs200AndDurationIs1() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("a".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }
}