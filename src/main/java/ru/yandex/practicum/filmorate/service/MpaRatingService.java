package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaRatingService {

    private final MpaRatingStorage mpaRatingStorage;

    public List<MpaRating> findAll() {
        return mpaRatingStorage.findAll();
    }

    public MpaRating findById(Long id) {
        return mpaRatingStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MPA rating with id " + id + " not found"));
    }
}