package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.List;

public interface FriendStorage {
    FriendshipStatus addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<Long> findFriendIds(long userId);

    List<Long> findCommonFriendIds(long userId, long otherId);
}