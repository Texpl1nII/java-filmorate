package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.model.User;

public class NameOrLoginValidator implements ConstraintValidator<NameOrLogin, User> {
    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            return true;
        }
        return true;
    }
}