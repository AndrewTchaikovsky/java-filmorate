package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.repository.BaseRepository;

import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String ADD_FRIENDS_QUERY = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIENDS_QUERY = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS_IDS_QUERY = "SELECT friend_id FROM friendships WHERE user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new UserRowMapper());
    }

    @Override
    public User addUser(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        getById(newUser.getId());
        update(UPDATE_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                Date.valueOf(newUser.getBirthday()),
                newUser.getId()
        );
        return newUser;
    }

    @Override
    public Collection<User> getUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User getById(Long id) {
        User user = findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
        user.setFriends(getFriendsIds(id));
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        jdbc.update(ADD_FRIENDS_QUERY, userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        jdbc.update(REMOVE_FRIENDS_QUERY, userId, friendId);
    }

    public Set<Long> getFriendsIds(Long userId) {
        return new HashSet<>(jdbc.query(
                GET_FRIENDS_IDS_QUERY,
                (rs, rowNum) -> rs.getLong("friend_id"),
                userId
        ));
    }

}
