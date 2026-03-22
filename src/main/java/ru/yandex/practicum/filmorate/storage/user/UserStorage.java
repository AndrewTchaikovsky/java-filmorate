package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User newUser);

    Collection<User> getUsers();

    User getById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Set<Long> getFriendsIds(Long userId);

    List<User> getUsersByIds(Collection<Long> userIds);

    List<User> getCommonFriends(Long userId, Long otherId);
}
