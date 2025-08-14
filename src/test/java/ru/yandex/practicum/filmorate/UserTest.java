package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassWhenValidUser() {
        User user = new User(null, "test@example.com", "testuser", "Test User",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Valid user should have no violations");
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        User user = new User(null, "", "testuser", "Test User",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Empty email should cause violation");
    }

    @Test
    void shouldFailWhenEmailLacksAt() {
        User user = new User(null, "invalid.email", "testuser", "Test User",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Email without @ should cause violation");
    }

    @Test
    void shouldFailWhenLoginIsEmptyString() {
        User user = new User(null, "test@example.com", "", "Test User",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Empty login should cause violation");
    }

    @Test
    void shouldFailWhenLoginIsEmptyOrHasSpaces() {
        User user = new User(null, "test@example.com", "test user", "Test User",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Login with spaces should cause violation");
    }

    @Test
    void shouldFailWhenFieldsAreNull() {
        User user = new User(null, null, null, null,
                LocalDate.of(2000, 1, 1), new HashSet<>());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Null email and login should cause violations");
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        User user = new User(null, "test@example.com", "testuser", "Test User",
                LocalDate.now().plusDays(1), new HashSet<>());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Future birthday should cause violation");
    }

    @Test
    void shouldPassWhenBirthdayIsToday() {
        User user = new User(null, "test@example.com", "testuser", "Test User",
                LocalDate.now(), new HashSet<>());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Birthday today should be valid");
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsNull() {
        User user = new User(null, "test@example.com", "testuser", null,
                LocalDate.of(2000, 1, 1), new HashSet<>());
        assertEquals("testuser", user.getName(), "Имя должно быть равно логину");
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        User user = new User(null, "test@example.com", "testuser", "",
                LocalDate.of(2000, 1, 1), new HashSet<>());
        assertEquals("testuser", user.getName(), "Имя должно стать равным логину");
    }
}