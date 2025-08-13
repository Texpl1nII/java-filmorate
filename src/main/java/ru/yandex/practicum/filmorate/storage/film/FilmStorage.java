package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Optional<Film> findById(int id);

    List<Film> findAll();

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getPopularFilms(int count);

    List<Film> getFilmsByGenreWithoutDuplicates(int genreId);

    List<Film> getFilmsByGenre(int genreId);
}