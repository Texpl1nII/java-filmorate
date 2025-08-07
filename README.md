# Filmorate

Filmorate is a Spring Boot application for managing films and users, including features like adding films, users, likes, and friendships, and retrieving popular films and common friends.

## Database Schema
<image-card alt="Database Schema" src="schema.png" ></image-card>

### Schema Explanation
The database schema supports the Filmorate application, storing users, films, genres, MPA ratings, likes, and friendships. Key tables:
- `users`: Stores user details (email, login, name, birthday).
- `films`: Stores film details (name, description, release date, duration, MPA rating).
- `mpa_ratings`: Stores MPA ratings (G, PG, PG-13, R, NC-17).
- `genres`: Stores genre names (Comedy, Drama, Cartoon, Thriller, Documentary, Action).
- `film_genres`: Links films to multiple genres.
- `likes`: Stores user likes for films.
- `friendships`: Stores user friendships with status (unconfirmed, confirmed).

The schema follows normalization rules:
- **1NF**: No arrays (genres, likes, friends in separate tables).
- **2NF/3NF**: Attributes depend only on primary keys, no transitive dependencies.

### Example SQL Queries

1. **Get All Films**
```sql
SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.name AS mpa_rating, GROUP_CONCAT(g.name) AS genres
FROM films f
LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id
LEFT JOIN film_genres fg ON f.film_id = fg.film_id
LEFT JOIN genres g ON fg.genre_id = g.genre_id
GROUP BY f.film_id;