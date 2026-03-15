package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmorateApplicationTests {
    private FilmController filmController;
    private FilmStorage filmStorage;
    private FilmService filmService;
    private UserController userController;
    private UserStorage userStorage;
    private UserService userService;
    private MpaStorage mpaStorage;
    private GenreStorage genreStorage;

    @BeforeEach
    void setup() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage, mpaStorage, genreStorage);
        userService = new UserService(userStorage);
        filmController = new FilmController(filmService);
        userController = new UserController(userService);
    }

    @Test
    void shouldAddValidFilm() {
        Film film = new Film();
        film.setName("Солтберн");
        film.setDescription("Студент Оксфорда одержимо втирается в семью богатых аристократов и постепенно захватывает их мир.");
        film.setReleaseDate(LocalDate.of(2023, 8, 31));
        film.setDuration(131);

        Film savedFilm = filmController.addFilm(film);

        assertNotNull(savedFilm.getId());
        assertEquals("Солтберн", savedFilm.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("Студент Оксфорда одержимо втирается в семью богатых аристократов и постепенно захватывает их мир.");
        film.setReleaseDate(LocalDate.of(2023, 8, 31));
        film.setDuration(131);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        Film film = new Film();
        film.setName("Солтберн");
        film.setDescription("Солтберн".repeat(100));
        film.setReleaseDate(LocalDate.of(2023, 8, 31));
        film.setDuration(131);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsTooEarly() {
        Film film = new Film();
        film.setName("Солтберн");
        film.setDescription("Студент Оксфорда одержимо втирается в семью богатых аристократов и постепенно захватывает их мир.");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(131);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        Film film = new Film();
        film.setName("Солтберн");
        film.setDescription("Студент Оксфорда одержимо втирается в семью богатых аристократов и постепенно захватывает их мир.");
        film.setReleaseDate(LocalDate.of(2023, 8, 31));
        film.setDuration(-131);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void shouldUpdateFilmCorrectly() {
        Film film = new Film();
        film.setName("Солтберн");
        film.setDescription("Студент Оксфорда одержимо втирается в семью богатых аристократов и постепенно захватывает их мир.");
        film.setReleaseDate(LocalDate.of(2023, 8, 31));
        film.setDuration(131);

        Film savedFilm = filmController.addFilm(film);

        Film update = new Film();
        update.setId(savedFilm.getId());
        update.setName("Saltburn");
        update.setDescription("An Oxford student obsessively ingratiates himself into a wealthy aristocratic family and gradually takes over their world.");
        update.setReleaseDate(LocalDate.of(2023, 11, 17));
        update.setDuration(132);

        Film updatedFilm = filmController.updateFilm(update);

        assertEquals("Saltburn", updatedFilm.getName());
        assertEquals(132, updatedFilm.getDuration());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithoutId() {
        Film film = new Film();

        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }

    @Test
    void shouldAddValidUser() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1989, 12, 28));

        User savedUser = userController.addUser(user);

        assertNotNull(savedUser.getId());
        assertEquals("testUser", savedUser.getLogin());
    }

    @Test
    void shouldUseLoginWhenNameIsBlank() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("testUser");
        user.setName(" ");
        user.setBirthday(LocalDate.of(1989, 12, 28));

        User savedUser = userController.addUser(user);

        assertEquals("testUser", savedUser.getName());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        User user = new User();
        user.setEmail("test.gmail.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1989, 12, 28));

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("test User");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1989, 12, 28));

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void shouldUpdateUserCorrectly() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1989, 12, 28));

        User savedUser = userController.addUser(user);

        User update = new User();
        update.setId(savedUser.getId());
        update.setEmail("updated@gmail.com");
        update.setLogin("updatedUser");
        update.setName("Updated User");
        update.setBirthday(LocalDate.of(1990, 12, 28));

        User updatedUser = userController.updateUser(update);

        assertEquals("Updated User", updatedUser.getName());
        assertEquals("updated@gmail.com", updatedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUserWithoutId() {
        User user = new User();

        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }


}
