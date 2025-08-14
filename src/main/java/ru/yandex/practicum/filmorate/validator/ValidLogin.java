package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message = "Login must not be empty")
@Pattern(regexp = "^\\S+$", message = "Login must not contain spaces")
public @interface ValidLogin {
    String message() default "Login must not be empty or contain spaces";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
