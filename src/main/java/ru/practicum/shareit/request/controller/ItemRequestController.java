package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDTO.Controller.ReturnItemRequestDTO addItemRequest(@RequestBody @Valid ItemRequestDTO.Controller.NewItemRequestDTO requestDTO,
                                                                         @RequestHeader("X-Sharer-User-Id") Optional<Long> idOwner) {
        return requestService.addRequest(ItemRequestDTO.Controller.Mapper.toItemRequest(requestDTO), idOwner.get());
    }

    @GetMapping
    public List<ItemRequestDTO.Controller.ReturnItemRequestDTO> getRequests(@RequestHeader("X-Sharer-User-Id") Optional<Long> idOwner) {
        return requestService.getRequestsByRequestorId(idOwner);
    }

    @GetMapping(path = "/{id}")
    public ItemRequestDTO.Controller.ReturnItemRequestDTO getRequestById(@RequestHeader("X-Sharer-User-Id") Optional<Long> idOwner,
                                                                         @PathVariable Long id) {
        return requestService.getItemRequest(idOwner.get(), id);
    }

    @GetMapping(path = "/all")    //Список запросов других пользователей
    public List<ItemRequestDTO.Controller.ReturnItemRequestDTO> getRequests(@RequestHeader("X-Sharer-User-Id") Optional<Long> idOwner,
                                                                            @RequestParam(defaultValue = "0") Integer from,
                                                                            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        if (size < 1) throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Wrong page size");
        return requestService.getRequestsByNotRequestorId(idOwner, from, size);
    }
}
