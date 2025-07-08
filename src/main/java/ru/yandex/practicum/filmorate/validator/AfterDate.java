package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AfterDateValidator.class)
public @interface AfterDate {
    String value();
    String message() default "Date must be on or after {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}