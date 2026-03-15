package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, UserDbStorage.class, MpaDbStorage.class, GenreDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmoRateJdbcTests {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Test
    void testAddFilm() {
        Film film = new Film();
        film.setName("Солтберн");
        film.setDescription("Студент Оксфорда одержимо втирается в семью богатых аристократов и постепенно захватывает их мир.");
        film.setReleaseDate(LocalDate.of(2023, 8, 31));
        film.setDuration(131);

        Mpa mpa = mpaDbStorage.findById(4);
        film.setMpa(mpa);

        Film savedFilm = filmDbStorage.addFilm(film);

        assertThat(savedFilm.getId()).isNotNull();
        assertThat(savedFilm.getName()).isEqualTo("Солтберн");
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Солтберн");
        film.setDescription("Студент Оксфорда одержимо втирается в семью богатых аристократов и постепенно захватывает их мир.");
        film.setReleaseDate(LocalDate.of(2023, 8, 31));
        film.setDuration(131);

        Mpa mpa = mpaDbStorage.findById(4);
        film.setMpa(mpa);

        Film savedFilm = filmDbStorage.addFilm(film);

        savedFilm.setName("Обновленный фильм");

        Film updatedFilm = filmDbStorage.updateFilm(savedFilm);
        assertThat(updatedFilm.getName()).isEqualTo("Обновленный фильм");
    }

    @Test
    void testGetFilms() {
        Film film = new Film();
        film.setName("Солтберн");
        film.setDescription("Студент Оксфорда одержимо втирается в семью богатых аристократов и постепенно захватывает их мир.");
        film.setReleaseDate(LocalDate.of(2023, 8, 31));
        film.setDuration(131);

        Mpa mpa = mpaDbStorage.findById(4);
        film.setMpa(mpa);

        filmDbStorage.addFilm(film);

        assertThat(filmDbStorage.getFilms()).isNotEmpty();
    }

    @Test
    void testAddLike() {
        Film film = new Film();
        film.setName("Солтберн");
        film.setDescription("Студент Оксфорда одержимо втирается в семью богатых аристократов и постепенно захватывает их мир.");
        film.setReleaseDate(LocalDate.of(2023, 8, 31));
        film.setDuration(131);
        Mpa mpa = mpaDbStorage.findById(4);
        film.setMpa(mpa);
        Film savedFilm = filmDbStorage.addFilm(film);

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1989, 12, 28));
        User savedUser = userDbStorage.addUser(user);

        filmDbStorage.addLike(savedFilm.getId(), savedUser.getId());

        Film retrieved = filmDbStorage.getById(savedFilm.getId());

        assertThat(retrieved.getLikes()).contains(savedUser.getId());
    }

    @Test
    void testRemoveLike() {
        Film film = new Film();
        film.setName("Солтберн");
        film.setDescription("Студент Оксфорда одержимо втирается в семью богатых аристократов и постепенно захватывает их мир.");
        film.setReleaseDate(LocalDate.of(2023, 8, 31));
        film.setDuration(131);
        Mpa mpa = mpaDbStorage.findById(4);
        film.setMpa(mpa);
        Film savedFilm = filmDbStorage.addFilm(film);

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1989, 12, 28));
        User savedUser = userDbStorage.addUser(user);

        filmDbStorage.addLike(savedFilm.getId(), savedUser.getId());
        Film retrieved = filmDbStorage.getById(savedFilm.getId());
        assertThat(retrieved.getLikes()).contains(savedUser.getId());

        filmDbStorage.removeLike(savedFilm.getId(), savedUser.getId());
        Film retrievedAfterRemoving = filmDbStorage.getById(savedFilm.getId());
        assertThat(retrievedAfterRemoving.getLikes()).doesNotContain(savedUser.getId());
    }

    @Test
    void testFindFilmById() {
        Film film = new Film();
        film.setName("Солтберн");
        film.setDescription("Студент Оксфорда одержимо втирается в семью богатых аристократов и постепенно захватывает их мир.");
        film.setReleaseDate(LocalDate.of(2023, 8, 31));
        film.setDuration(131);

        Mpa mpa = mpaDbStorage.findById(4);
        film.setMpa(mpa);

        Film savedFilm = filmDbStorage.addFilm(film);

        Film retrievedFilm = filmDbStorage.getById(savedFilm.getId());

        assertThat(retrievedFilm.getId()).isEqualTo(savedFilm.getId());
    }

    @Test
    void testFindFilmByNotExistingId() {
        assertThatThrownBy(() -> filmDbStorage.getById(111L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testAddUser() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1989, 12, 28));

        User savedUser = userDbStorage.addUser(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1989, 12, 28));

        User savedUser = userDbStorage.addUser(user);
        savedUser.setName("Обновленный пользователь");

        User updatedUser = userDbStorage.updateUser(savedUser);

        assertThat(updatedUser.getName()).isEqualTo("Обновленный пользователь");
    }

    @Test
    void testGetUsers() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1989, 12, 28));

        userDbStorage.addUser(user);
        assertThat(userDbStorage.getUsers()).isNotEmpty();
    }

    @Test
    void testFindUserById() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1989, 12, 28));

        User savedUser = userDbStorage.addUser(user);

        User retrievedUser = userDbStorage.getById(savedUser.getId());

        assertThat(retrievedUser.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void testFindUserByNotExistingId() {
        assertThatThrownBy(() -> userDbStorage.getById(111L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindAllMpa() {
        List<Mpa> allRatings = mpaDbStorage.findAll();
        assertThat(allRatings).isNotEmpty();
    }

    @Test
    void testFindMpaById() {
        Mpa mpa = mpaDbStorage.findById(1);
        assertThat(mpa.getId()).isEqualTo(1);
    }

    @Test
    void testFindMpaByNotExistingId() {
        assertThrows(NotFoundException.class, () -> mpaDbStorage.findById(555));
    }

    @Test
    void testFindAllGenres() {
        List<Genre> allGenres = genreDbStorage.findAll();
        assertThat(allGenres).isNotEmpty();
    }

    @Test
    void testFindGenreById() {
        Genre genre = genreDbStorage.findById(1);
        assertThat(genre.getId()).isEqualTo(1);
    }

    @Test
    void testFindGenreByNotExistingId() {
        assertThrows(NotFoundException.class, () -> genreDbStorage.findById(555));
    }

}
