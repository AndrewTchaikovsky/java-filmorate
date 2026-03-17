package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    Collection<Film> getFilms();

    Film getById(Long id);

    Set<Long> getLikes(long filmId);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> getPopularFilms(int count);
}