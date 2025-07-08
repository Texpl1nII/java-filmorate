package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameOrLoginValidator.class)
public @interface NameOrLogin {
    String message() default "Name must not be blank or will be set to login";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}