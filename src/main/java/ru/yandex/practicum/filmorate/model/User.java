package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.NameOrLogin;
import ru.yandex.practicum.filmorate.validator.NotFutureDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NameOrLogin
public class User {
    private Integer id;
    @NotBlank(message = "Email must be specified")
    @Email(message = "Email must contain '@'")
    private String email;
    @NotBlank(message = "Login must not be empty")
    private String login;
    private String name;
    @NotFutureDate
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
}