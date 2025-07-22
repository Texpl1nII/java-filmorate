package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    @Email(message = "Email must be a valid email address")
    private String email;
    @ValidLogin
    private String login;
    private String name;
    @NotFutureDate
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    @Constraint(validatedBy = LoginValidator.class)
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidLogin {
        String message() default "Login must not be empty or contain spaces";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class LoginValidator implements ConstraintValidator<ValidLogin, String> {
        @Override
        public boolean isValid(String login, ConstraintValidatorContext context) {
            if (login == null || login.isBlank()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Login must not be empty")
                        .addConstraintViolation();
                return false;
            }
            if (login.contains(" ")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Login must not contain spaces")
                        .addConstraintViolation();
                return false;
            }
            return true;
        }
    }
}