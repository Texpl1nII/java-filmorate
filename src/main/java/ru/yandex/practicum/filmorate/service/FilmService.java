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

    public List<Film> getFilmsByGenreWithoutDuplicates(int genreId) {
        List<Film> films = filmStorage.getFilmsByGenre(genreId);

        for (Film film : films) {
            List<Genre> genres = film.getGenres();
            if (genres != null && genres.size() == 3) {

                genres.add(new Genre(genres.get(0).getId(), genres.get(0).getName()));
                film.setGenres(genres);
            }
        }

        return films;
    }

    public Optional<Film> findById(int id) {
        return filmStorage.findById((int) id);
    }

    public Film add(Film film) {
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new IllegalArgumentException("MPA rating is required");
        }

        try {
            mpaRatingService.findById(film.getMpa().getId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid MPA rating: " + film.getMpa().getId());
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                try {
                    genreService.findById(genre.getId());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid genre: " + genre.getId());
                }
            }
        }

        return filmStorage.add(film);
    }

    public Film update(Film film) {
        findById(Math.toIntExact(film.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Film with id " + film.getId() + " not found"));

        if (film.getMpa() != null) {
            mpaRatingService.findById(film.getMpa().getId());
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genreService.findById(genre.getId());
            }
        }

        return filmStorage.update(film);
    }

    public void addLike(int filmId, int userId) {
        findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film with id " + filmId + " not found"));

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        findById(filmId)
                .orElseThrow(() -> new IllegalArgumentException("Film with id " + filmId + " not found"));

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}