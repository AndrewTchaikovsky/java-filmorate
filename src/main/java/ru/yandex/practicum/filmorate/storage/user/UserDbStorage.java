package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.repository.BaseRepository;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String ADD_FRIENDS_QUERY = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIENDS_QUERY = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS_IDS_QUERY = "SELECT friend_id FROM friendships WHERE user_id = ?";
    private static final String GET_USERS_BY_IDS_QUERY = "SELECT * FROM users WHERE id IN (%s)";
    private static final String GET_COMMON_FRIENDS_QUERY =
            """
                    SELECT u.*
                    FROM users u
                    JOIN friendships f1 ON u.id = f1.user_id
                    JOIN friendships f2 ON u.id = f2.user_id
                    WHERE f1.user_id = ?
                    AND f2.user_id = ?
                    """;

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
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbc.update(ADD_FRIENDS_QUERY, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbc.update(REMOVE_FRIENDS_QUERY, userId, friendId);
    }

    @Override
    public Set<Long> getFriendsIds(Long userId) {
        return new HashSet<>(jdbc.query(
                GET_FRIENDS_IDS_QUERY,
                (rs, rowNum) -> rs.getLong("friend_id"),
                userId
        ));
    }

    @Override
    public List<User> getUsersByIds(Collection<Long> userIds) {
        if (userIds.isEmpty()) return Collections.emptyList();

        String placeholder = userIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));
        String query = String.format(GET_USERS_BY_IDS_QUERY, placeholder);
        return jdbc.query(query, new UserRowMapper(), userIds.toArray());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        return jdbc.query(GET_COMMON_FRIENDS_QUERY, new UserRowMapper(), userId, otherId);
    }

}
