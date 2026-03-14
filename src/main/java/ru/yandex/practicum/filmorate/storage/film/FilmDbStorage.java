package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.repository.BaseRepository;

import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private final GenreRowMapper genreRowMapper = new GenreRowMapper();
    private static final String FIND_ALL_QUERY =
            """
                    SELECT f.*, m.id AS mpa_id, m.name AS mpa_name
                    FROM films f
                    JOIN mpa_ratings m ON f.mpa_id = m.id
                    ORDER BY f.id
                    """;
    private static final String FIND_BY_ID_QUERY =
            """
                    SELECT f.*, m.id AS mpa_id, m.name AS mpa_name
                    FROM films f
                    JOIN mpa_ratings m ON f.mpa_id = m.id
                    WHERE f.id = ?
                    """;
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String ADD_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String GET_FILM_GENRES =
            """
                    SELECT g.id,
                           g.name
                            FROM genres g
                            JOIN film_genres fg ON fg.genre_id = g.id
                            WHERE fg.film_id = ?
                    """;
    private static final String ADD_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_LIKES = "SELECT user_id FROM likes WHERE film_id = ?";

    public FilmDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new FilmRowMapper());
    }

    @Override
    public Film addFilm(Film film) {
        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        saveGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        update(UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        jdbc.update(DELETE_FILM_GENRES, newFilm.getId());
        saveGenres(newFilm);
        return newFilm;
    }

    @Override
    public Collection<Film> getFilms() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        for (Film film : films) {
            film.setGenres(getGenres(film.getId()));
            film.setLikes(getLikes(film.getId()));
        }
        return films;
    }

    @Override
    public Film getById(Long id) {
        Film film = findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден."));
        film.setGenres(getGenres(id));
        film.setLikes(getLikes(id));
        return film;

    }

    public void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        for (Genre genre : film.getGenres()) {
            update(ADD_GENRE, film.getId(), genre.getId());
        }
    }

    public Set<Genre> getGenres(long filmId) {
        return new HashSet<>(jdbc.query(GET_FILM_GENRES, genreRowMapper, filmId));
    }

    public Set<Long> getLikes(long filmId) {
        return new HashSet<>(jdbc.query(GET_LIKES, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }

    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE, filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        jdbc.update(DELETE_LIKE, filmId, userId);
    }

}
