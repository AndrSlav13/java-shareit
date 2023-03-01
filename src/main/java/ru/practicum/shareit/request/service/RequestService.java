package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    ItemRequestDTO.Controller.ReturnItemRequestDTO addRequest(ItemRequest request, Long idOwner);

    List<ItemRequestDTO.Controller.ReturnItemRequestDTO> getRequestsByRequestorId(Optional<Long> idOwner);

    List<ItemRequestDTO.Controller.ReturnItemRequestDTO> getRequestsByNotRequestorId(Optional<Long> idOwner, Integer from, Integer size);

    ItemRequest getSimpleItemRequest(Long id);

    public ItemRequestDTO.Controller.ReturnItemRequestDTO getItemRequest(Long idOwner, Long idRequest);
}
