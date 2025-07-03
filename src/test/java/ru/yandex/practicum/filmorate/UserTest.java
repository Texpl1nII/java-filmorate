package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void shouldFailWhenEmailIsEmpty() {
        User user = new User();
        user.setLogin("testLogin");
        assertThrows(ValidationException.class, user::validate, "Email must be specified and contain '@'");
    }

    @Test
    void shouldFailWhenEmailLacksAt() {
        User user = new User();
        user.setEmail("invalid.email");
        user.setLogin("testLogin");
        assertThrows(ValidationException.class, user::validate, "Email must be specified and contain '@'");
    }

    @Test
    void shouldFailWhenLoginIsEmptyOrHasSpaces() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test login");
        assertThrows(ValidationException.class, user::validate, "Login must not be empty or contain spaces");
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("");
        user.validate();
        assertEquals("testLogin", user.getName());
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, user::validate, "Birthday cannot be in the future");
    }

    @Test
    void shouldPassWhenValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        assertDoesNotThrow(user::validate);
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsNull() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.validate();
        assertEquals("testLogin", user.getName());
    }
}