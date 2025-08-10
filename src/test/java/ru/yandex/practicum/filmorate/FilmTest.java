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
        this.validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenReleaseDateBefore1895() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Фильм с неверной датой выпуска должен нарушать правила.");
        assertEquals(1, violations.size(), "Ожидается ровно одно нарушение правил.");
        assertTrue(violations.iterator().next().getMessage().contains("Дата выпуска"),
                "Ошибка должна указывать на проблему с датой выпуска.");
    }

    @Test
    void shouldPassWhenReleaseDateOnOrAfter1895() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);

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

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Пустое название нарушает правило.");
        assertEquals(1, violations.size(), "Ожидаем одну ошибку.");
        assertTrue(violations.iterator().next().getMessage().contains("Название"),
                "Ошибка должна относиться к названию фильма.");
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Слишком длинное описание нарушает правило.");
        assertEquals(1, violations.size(), "Ожидаем одну ошибку.");
        assertTrue(violations.iterator().next().getMessage().contains("Описание"),
                "Ошибка должна касаться описания фильма.");
    }

    @Test
    void shouldFailWhenDurationNotPositive() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Отрицательная длительность нарушает правило.");
        assertEquals(1, violations.size(), "Ожидаем одну ошибку.");
        assertTrue(violations.iterator().next().getMessage().contains("Продолжительность"),
                "Ошибка должна указывать на неправильность продолжительности.");
    }
}