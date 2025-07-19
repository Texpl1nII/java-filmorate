package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    public FilmController(FilmService filmService, FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Returning all films, count: {}", filmStorage.findAll().size());
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable int id) {
        log.debug("Finding film with id: {}", id);
        return filmStorage.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Film with id " + id + " not found"));
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Creating new film: {}", film.getName());
        return filmStorage.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Updating film with id: {}", film.getId());
        if (film.getId() == null || film.getId() <= 0) {
            log.error("Invalid film ID: {}", film.getId());
            throw new ValidationException("Film ID must be specified and positive");
        }
        return filmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("User {} liking film {}", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("User {} removing like from film {}", userId, id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.debug("Requesting top {} popular films", count);
        return filmService.getPopularFilms(count);
    }
}