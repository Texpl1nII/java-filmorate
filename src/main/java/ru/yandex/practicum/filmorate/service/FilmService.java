package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaRatingService mpaRatingService;
    private final GenreService genreService;
    private final UserService userService;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            MpaRatingService mpaRatingService,
            GenreService genreService,
            @Lazy UserService userService) {
        this.filmStorage = filmStorage;
        this.mpaRatingService = mpaRatingService;
        this.genreService = genreService;
        this.userService = userService;
    }

    public Film add(Film film) {
        validateFilm(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        findById(film.getId());
        return filmStorage.update(film);
    }

    private void validateFilm(Film film) {
        if (film.getMpa() != null) {
            mpaRatingService.findById(film.getMpa().getId());
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreService.findById(genre.getId());
            }
        }
    }

    public List<Film> getFilmsByGenre(Long genreId) {
        return filmStorage.getFilmsByGenre(genreId);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Film with id " + id + " not found"));
    }

    public void addLike(Long filmId, Long userId) {
        findById(filmId);
        userService.findById(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        findById(filmId);
        userService.findById(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}