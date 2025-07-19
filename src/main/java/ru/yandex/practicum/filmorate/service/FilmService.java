package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film with id " + filmId + " not found"));
        userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        film.getLikes().add((long) userId);
        log.info("User {} liked film {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film with id " + filmId + " not found"));
        userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        if (!film.getLikes().contains((long) userId)) {
            throw new IllegalArgumentException("User " + userId + " has not liked film " + filmId);
        }
        film.getLikes().remove((long) userId);
        log.info("User {} removed like from film {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Returning top {} popular films", count);
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
