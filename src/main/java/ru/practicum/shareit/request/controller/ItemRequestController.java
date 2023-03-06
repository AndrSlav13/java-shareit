package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDTO.Controller.ReturnItemRequestDTO addItemRequest(@RequestBody @Valid ItemRequestDTO.Controller.NewItemRequestDTO requestDTO,
                                                                         @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return requestService.addRequest(ItemRequestDTO.Controller.Mapper.toItemRequest(requestDTO), idOwner);
    }

    @GetMapping
    public List<ItemRequestDTO.Controller.ReturnItemRequestDTO> getRequests(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return requestService.getRequestsByRequestorId(idOwner);
    }

    @GetMapping(path = "/{id}")
    public ItemRequestDTO.Controller.ReturnItemRequestDTO getRequestById(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner,
                                                                         @NotNull @Positive @PathVariable Long id) {
        return requestService.getItemRequest(idOwner, id);
    }

    @GetMapping(path = "/all")    //Список запросов других пользователей
    public List<ItemRequestDTO.Controller.ReturnItemRequestDTO> getRequests(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner,
                                                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                            @Positive @RequestParam(defaultValue = "10") Integer size) {

        return requestService.getRequestsByNotRequestorId(idOwner, from, size);
    }
}
