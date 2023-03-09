package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDTO.Controller.ReturnItemRequestDTO addItemRequest(@RequestBody ItemRequestDTO.Controller.NewItemRequestDTO requestDTO,
                                                                         @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return requestService.addRequest(ItemRequestDTO.Controller.Mapper.toItemRequest(requestDTO), idOwner);
    }

    @GetMapping
    public List<ItemRequestDTO.Controller.ReturnItemRequestDTO> getRequests(@RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return requestService.getRequestsByRequestorId(idOwner);
    }

    @GetMapping(path = "/{id}")
    public ItemRequestDTO.Controller.ReturnItemRequestDTO getRequestById(@RequestHeader("X-Sharer-User-Id") Long idOwner,
                                                                         @PathVariable Long id) {
        return requestService.getItemRequest(idOwner, id);
    }

    @GetMapping(path = "/all")    //Список запросов других пользователей
    public List<ItemRequestDTO.Controller.ReturnItemRequestDTO> getRequests(@RequestHeader("X-Sharer-User-Id") Long idOwner,
                                                                            @RequestParam(defaultValue = "0") Integer from,
                                                                            @RequestParam(defaultValue = "10") Integer size) {

        return requestService.getRequestsByNotRequestorId(idOwner, from, size);
    }
}
