package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage, @Qualifier("mpaDbStorage") MpaStorage mpaStorage, @Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Film addFilm(Film film) {
        validateFilm(film);

        if (film.getMpa() != null) {
            mpaStorage.findById(film.getMpa().getId());
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Integer> genreIds = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            Set<Genre> genres = genreStorage.getGenresByIds(genreIds);

            if (genres.size() != genreIds.size()) {
                throw new NotFoundException("Жанр не найден");
            }

            film.setGenres(genres);
        }
        Film savedFilm = filmStorage.addFilm(film);
        return filmStorage.getById(savedFilm.getId());
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        filmStorage.getById(newFilm.getId());
        validateFilm(newFilm);
        if (newFilm.getMpa() != null) {
            mpaStorage.findById(newFilm.getMpa().getId());
        }
        return filmStorage.updateFilm(newFilm);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getById(Long id) {
        return filmStorage.getById(id);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.getById(filmId);
        userStorage.getById(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new NotFoundException("Такой фильм не найден");
        }
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new NotFoundException("Такой пользователь не найден");
        }
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
