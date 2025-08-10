package ru.yandex.practicum.filmorate.model;

import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Valid
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата выпуска не может быть пустой")
    @FilmReleaseDate(message = "Дата выпуска не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    private Integer mpaRatingId;

    private Set<Integer> genreIds = new HashSet<>();

    private Set<Long> likes = new HashSet<>();

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = FilmReleaseDateValidator.class)
    public @interface FilmReleaseDate {
        String message() default "Дата выпуска не может быть раньше 28 декабря 1895 года.";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class FilmReleaseDateValidator implements ConstraintValidator<FilmReleaseDate, LocalDate> {
        private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

        @Override
        public void initialize(FilmReleaseDate constraintAnnotation) {
        }

        @Override
        public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
            return value != null && !value.isBefore(EARLIEST_RELEASE_DATE);
        }
    }
}