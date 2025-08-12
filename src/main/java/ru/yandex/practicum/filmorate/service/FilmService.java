package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final MpaRatingService mpaRatingService;
    private final GenreService genreService;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            MpaRatingService mpaRatingService,
            GenreService genreService) {
        this.filmStorage = filmStorage;
        this.mpaRatingService = mpaRatingService;
        this.genreService = genreService;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Optional<Film> findById(int id) {
        return filmStorage.findById((long) id);
    }

    public Film add(Film film) {
        // Проверка существования MPA
        if (film.getMpa() != null) {
            mpaRatingService.findById(film.getMpa().getId());
        }

        // Проверка существования жанров
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genreService.findById(genre.getId());
            }
        }

        return filmStorage.add(film);
    }

    public Film update(Film film) {
        // Проверка существования фильма
        findById(Math.toIntExact(film.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Film with id " + film.getId() + " not found"));

        // Проверка существования MPA
        if (film.getMpa() != null) {
            mpaRatingService.findById(film.getMpa().getId());
        }

        // Проверка существования жанров
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genreService.findById(genre.getId());
            }
        }

        return filmStorage.update(film);
    }

    public void addLike(int filmId, int userId) {
        // Проверка существования фильма
        findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film with id " + filmId + " not found"));

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        // Проверка существования фильма
        findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film with id " + filmId + " not found"));

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}