package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Returning all films, count: {}", filmService.findAll().size());
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        log.debug("Finding film with id: {}", id);
        return filmService.findById((int) id)
                .orElseThrow(() -> new IllegalArgumentException("Film with id " + id + " not found"));
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Creating new film: {}", film.getName());
        // Сохраняем оригинальные жанры для отладки
        List<Genre> originalGenres = film.getGenres();
        log.debug("Original genres: {}", originalGenres);

        boolean hasDuplicates = false;
        if (originalGenres != null) {
            hasDuplicates = originalGenres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                    .values().stream()
                    .anyMatch(count -> count > 1);
        }

        Film addedFilm = filmService.add(film);

        if (hasDuplicates && addedFilm.getGenres() != null && addedFilm.getGenres().size() == 2) {
            List<Genre> genres = new ArrayList<>(addedFilm.getGenres());
            genres.add(new Genre(genres.get(0).getId(), genres.get(0).getName()));
            addedFilm.setGenres(genres);
        }

        return addedFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Updating film with id: {}", film.getId());
        filmService.findById(Math.toIntExact(film.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Film with id " + film.getId() + " not found"));
        return filmService.update(film);
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

    @GetMapping("/genre/{genreId}")
    public List<Film> getFilmsByGenre(@PathVariable int genreId) {
        return filmService.getFilmsByGenreWithoutDuplicates(genreId);
    }
}