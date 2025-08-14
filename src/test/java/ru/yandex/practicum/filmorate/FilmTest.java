package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenReleaseDateBefore1895() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G"));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Фильм с неверной датой выпуска должен нарушать правила.");
    }

    @Test
    void shouldPassWhenReleaseDateOnOrAfter1895() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G"));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Правильная дата выпуска не должна приводить к нарушениям.");
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G"));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Пустое название нарушает правило.");
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G"));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Слишком длинное описание нарушает правило.");
    }

    @Test
    void shouldFailWhenDurationNotPositive() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-1);
        film.setMpa(new MpaRating(1L, "G"));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Отрицательная длительность нарушает правило.");
    }

    @Test
    void shouldFailWhenMpaIsNull() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Null MPA should violate the constraint.");
    }
}