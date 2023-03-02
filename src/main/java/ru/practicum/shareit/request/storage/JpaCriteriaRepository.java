package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface JpaCriteriaRepository {
    List<ItemRequest> findAllByNotRequestorId(Long requestorId, Integer from, Integer size);

    List<ItemRequest> findAllByRequestorId(Long requestorId);
}
