package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        User user = new User();
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation should fail for empty email");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                v.getMessage().equals("Email must be specified")), "Expected 'Email must be specified' error");
    }

    @Test
    void shouldFailWhenEmailLacksAt() {
        User user = new User();
        user.setEmail("invalid.email");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation should fail for invalid email format");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                v.getMessage().equals("Email must be a valid email address")), "Expected 'Email must be a valid email address' error");
    }

    @Test
    void shouldFailWhenLoginIsEmptyOrHasSpaces() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test login");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation should fail for login with spaces");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login") &&
                v.getMessage().equals("Login must not contain spaces")), "Expected 'Login must not contain spaces' error");
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Validation should pass for valid user");
        assertEquals("testLogin", user.getName(), "Name should be set to login when empty");
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation should fail for future birthday");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday") &&
                v.getMessage().equals("Birthday cannot be in the future")), "Expected 'Birthday cannot be in the future' error");
    }

    @Test
    void shouldPassWhenValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Validation should pass for valid user");
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsNull() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Validation should pass for valid user");
        assertEquals("testLogin", user.getName(), "Name should be set to login when null");
    }

    @Test
    void shouldFailWhenFieldsAreNull() {
        User user = new User();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation should fail for null fields");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email") &&
                v.getMessage().equals("Email must be specified")), "Expected 'Email must be specified' error");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login") &&
                v.getMessage().equals("Login must not be empty")), "Expected 'Login must not be empty' error");
    }

    @Test
    void shouldFailWhenLoginIsEmptyString() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation should fail for empty login");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login") &&
                v.getMessage().equals("Login must not be empty")), "Expected 'Login must not be empty' error");
    }

    @Test
    void shouldPassWhenBirthdayIsToday() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.now());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Validation should pass for valid user with today’s birthday");
    }
}