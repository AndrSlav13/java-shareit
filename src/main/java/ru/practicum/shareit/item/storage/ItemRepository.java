package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long id);

    Optional<Item> findItemById(Long id);

    List<Item> findByDescriptionContainsIgnoreCaseAndAvailableIsTrueOrNameContainsIgnoreCaseAndAvailableIsTrue(String text1, String text2, Pageable pag);

    List<Item> findItemByOwner(User owner, Pageable pag);
}