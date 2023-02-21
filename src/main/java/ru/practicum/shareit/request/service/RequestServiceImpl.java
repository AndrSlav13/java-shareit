package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestStore;

@Service
@Transactional
public class RequestServiceImpl implements RequestService {
    private RequestStore requestStore;

    public RequestServiceImpl(RequestStore requestStore) {
        this.requestStore = requestStore;
    }

    @Override
    public Long addRequest(ItemRequest request) {
        return requestStore.addRequest(ItemRequestDTO.Mapper.toDTO(request));
    }
}
