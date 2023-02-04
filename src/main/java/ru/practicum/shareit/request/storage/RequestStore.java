package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.dto.ItemRequestDTO;

public interface RequestStore {
    Integer addRequest(ItemRequestDTO.NewReturnItemRequestDTO requestDTO);
}
