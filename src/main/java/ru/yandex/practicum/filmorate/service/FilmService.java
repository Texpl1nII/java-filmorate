package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film add(Film film) {
        log.debug("Adding film: {}", film.getName());
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        log.debug("Updating film with id: {}", film.getId());
        return filmStorage.update(film);
    }

    public Optional<Film> findById(int id) {
        log.debug("Finding film with id: {}", id);
        return filmStorage.findById(id);
    }

    public List<Film> findAll() {
        log.debug("Returning all films, count: {}", filmStorage.findAll().size());
        return filmStorage.findAll();
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilmOrThrow(filmId);
        userService.getUserOrThrow(userId);
        film.getLikes().add((long) userId);
        log.info("User {} liked film {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilmOrThrow(filmId);
        userService.getUserOrThrow(userId);
        if (!film.getLikes().contains((long) userId)) {
            throw new IllegalArgumentException("User " + userId + " has not liked film " + filmId);
        }
        film.getLikes().remove((long) userId);
        log.info("User {} removed like from film {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            log.error("Invalid count for popular films: {}", count);
            throw new ValidationException("Count must be positive");
        }
        log.info("Returning top {} popular films", count);
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(int id) {
        Optional<Film> film = filmStorage.findById(id);
        if (film.isEmpty()) {
            log.error("Film with id {} not found", id);
            throw new IllegalArgumentException("Film with id " + id + " not found");
        }
        return film.get();
    }
}
