package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {
    private LocalDate earliestDate;

    @Override
    public void initialize(AfterDate constraintAnnotation) {
        earliestDate = LocalDate.parse(constraintAnnotation.value(), DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date == null || !date.isBefore(earliestDate);
    }
}