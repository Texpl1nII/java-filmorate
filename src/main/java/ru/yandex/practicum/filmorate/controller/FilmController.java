package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final ConcurrentHashMap<Integer, Film> films = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    @GetMapping
    public List<Film> findAll() {
        log.info("Returning all films, count: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Creating new film: {}", film.getName());
        film.setId(idGenerator.incrementAndGet());
        films.put(film.getId(), film);
        log.info("Created film with id: {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Updating film with id: {}", film.getId());
        if (film.getId() == null || film.getId() <= 0) {
            log.error("Invalid film ID: {}", film.getId());
            throw new IllegalArgumentException("Film ID must be specified and positive");
        }
        if (!films.containsKey(film.getId())) {
            log.error("Film with id {} not found", film.getId());
            throw new IllegalArgumentException("Film with id " + film.getId() + " not found");
        }
        films.put(film.getId(), film);
        log.info("Updated film with id: {}", film.getId());
        return film;
    }
}