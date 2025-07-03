package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

@Data
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public void validate() {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Email must be specified and contain '@'");
        }
        if (login == null || login.isBlank() || login.contains(" ")) {
            throw new ValidationException("Login must not be empty or contain spaces");
        }
        if (name == null || name.isBlank()) {
            name = login;
        }
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Birthday cannot be in the future");
        }
    }
}