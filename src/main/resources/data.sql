DELETE FROM genres;
DELETE FROM mpa_ratings;
INSERT INTO genres (name) VALUES
    ('Comedy'),
    ('Drama'),
    ('Animation'),
    ('Thriller'),
    ('Documentary'),
    ('Militant');
INSERT INTO mpa_ratings (name) VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17');