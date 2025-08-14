package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre findById(Long id) {
        return genreStorage.findById(Math.toIntExact(id))
                .orElseThrow(() -> new EntityNotFoundException("Genre with id " + id + " not found"));
    }
}