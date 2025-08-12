DELETE FROM film_genres;
DELETE FROM likes;
DELETE FROM films;
DELETE FROM mpa_ratings;
DELETE FROM genres;

ALTER TABLE mpa_ratings ALTER COLUMN mpa_rating_id RESTART WITH 1;
ALTER TABLE genres ALTER COLUMN genre_id RESTART WITH 1;

INSERT INTO mpa_ratings (name) VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17');

INSERT INTO genres (name) VALUES
    ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик');