package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestBody @Valid ItemRequestDTO.Controller.NewItemRequestDTO requestDTO,
                                                 @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return requestClient.addRequest(requestDTO, idOwner);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsOfUser(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        return requestClient.getRequestsOfUser(idOwner, from, size);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getRequestById(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner,
                                                                         @NotNull @Positive @PathVariable Long id) {
        return requestClient.getRequest(idOwner, id);
    }

    @GetMapping(path = "/all")    //Список запросов других пользователей
    public ResponseEntity<Object> getRequestsOfNotUser(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner,
                                                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                            @Positive @RequestParam(defaultValue = "10") Integer size) {

        return requestClient.getRequestsOfNotUser(idOwner, from, size);
    }
}
