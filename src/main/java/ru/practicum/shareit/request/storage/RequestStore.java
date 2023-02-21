package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.dto.ItemRequestDTO;

public interface RequestStore {
    Long addRequest(ItemRequestDTO.NewReturnItemRequestDTO requestDTO);
}
