package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private RequestRepository requestStore;
    private UserService userService;

    @Autowired
    public RequestServiceImpl(RequestRepository requestStore, UserService userService) {
        this.requestStore = requestStore;
        this.userService = userService;
    }

    @Transactional
    @Override
    public ItemRequest getSimpleItemRequest(Long id) {
        return requestStore.findById(id).orElseThrow(() ->
                new HttpCustomException(HttpStatus.NOT_FOUND, "Item request with id=" + id + " is absent")
        );
    }

    @Transactional
    @Override
    public ItemRequestDTO.Controller.ReturnItemRequestDTO getItemRequest(Long idOwner, Long idRequest) {
        User user = userService.getSimpleUser(idOwner);
        UserService.validate(user);

        ItemRequest request = getSimpleItemRequest(idRequest);
        return ItemRequestDTO.Controller.Mapper.toDTO(request);
    }

    @Transactional
    @Override
    public ItemRequestDTO.Controller.ReturnItemRequestDTO addRequest(ItemRequest request, Long idOwner) {
        request.setId(null);
        User user = userService.getSimpleUser(idOwner);
        UserService.validate(user);

        request.setRequestor(user);
        requestStore.save(request);
        user.addRequest(request);
        return ItemRequestDTO.Controller.Mapper.toDTO(request);
    }

    @Transactional
    @Override
    public List<ItemRequestDTO.Controller.ReturnItemRequestDTO> getRequestsByRequestorId(Long idOwner) {
        User user = userService.getSimpleUser(idOwner);
        UserService.validate(user);

        return requestStore.findAllByRequestorId(idOwner).stream()
                .map(it -> ItemRequestDTO.Controller.Mapper.toDTO(it)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<ItemRequestDTO.Controller.ReturnItemRequestDTO> getRequestsByNotRequestorId(Long idOwner, Integer from, Integer size) {

        return requestStore.findAllByNotRequestorId(idOwner, from, size).stream()
                .map(it -> ItemRequestDTO.Controller.Mapper.toDTO(it)).collect(Collectors.toList());
    }
}