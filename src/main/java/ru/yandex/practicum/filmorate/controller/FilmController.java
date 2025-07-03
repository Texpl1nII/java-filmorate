package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final ConcurrentHashMap<Integer, Film> films = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Returning all films, count: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.debug("Creating new film: {}", film.getName());
        film.validate();
        film.setId(idGenerator.incrementAndGet());
        films.put(film.getId(), film);
        log.info("Created film with id: {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.debug("Updating film with id: {}", film.getId());
        if (film.getId() == null || film.getId() <= 0) {
            log.error("Invalid film ID: {}", film.getId());
            throw new IllegalArgumentException("Film ID must be specified and positive");
        }
        film.validate();
        if (!films.containsKey(film.getId())) {
            log.error("Film with id {} not found", film.getId());
            throw new IllegalArgumentException("Film with id " + film.getId() + " not found");
        }
        films.put(film.getId(), film);
        log.info("Updated film with id: {}", film.getId());
        return film;
    }
}