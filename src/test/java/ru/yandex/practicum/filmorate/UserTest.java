package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Valid user should have no violations");
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        User user = new User();
        user.setEmail("");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Empty email should cause violation");
    }

    @Test
    void shouldFailWhenEmailLacksAt() {
        User user = new User();
        user.setEmail("invalid.email");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Email without @ should cause violation");
    }

    @Test
    void shouldFailWhenLoginIsEmptyString() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Empty login should cause violation");
    }

    @Test
    void shouldFailWhenLoginIsEmptyOrHasSpaces() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test user");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Login with spaces should cause violation");
    }

    @Test
    void shouldFailWhenFieldsAreNull() {
        User user = new User();
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Null email and login should cause violations");
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Future birthday should cause violation");
    }

    @Test
    void shouldPassWhenBirthdayIsToday() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Birthday today should be valid");
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsNull() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Valid user with null name should pass");
        assertEquals("testuser", user.getName(), "Name should default to login");
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Valid user with empty name should pass");
        assertEquals("testuser", user.getName(), "Name should default to login");
    }
}