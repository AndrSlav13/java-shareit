package ru.practicum.shareit.user.storage;

public interface UserCriteriaRepository {
    boolean isEarlierItemBookedByUser(Long itemId, Long userId);

    boolean isUserBooker(Long itemId, Long userId);

    boolean isUserOwner(Long itemId, Long userId);

    boolean isUserOwnerOrBooker(Long itemId, Long userId);

    boolean isUserAbleToBook(Long itemId, Long userId);
}