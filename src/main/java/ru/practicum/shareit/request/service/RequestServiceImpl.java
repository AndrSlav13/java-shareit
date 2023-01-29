package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestStore;

@Service
public class RequestServiceImpl implements RequestService {
    private RequestStore requestStore;

    public RequestServiceImpl(RequestStore requestStore) {
        this.requestStore = requestStore;
    }

    public Integer addRequest(ItemRequest request) {
        return requestStore.addRequest(ItemRequestDTO.Mapper.toDTO(request));
    }
}
