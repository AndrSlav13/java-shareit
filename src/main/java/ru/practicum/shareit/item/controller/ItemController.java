package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDTO.Controller.ReturnItemDTO addItem(@RequestBody @Valid ItemDTO.Controller.NewItemDTO itemDTO,
                                                    @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return itemService.addItem(ItemDTO.Controller.Mapper.toItem(itemDTO), idOwner, itemDTO.getRequestId());
    }


    @PatchMapping("/{itemId}")
    public ItemDTO.Controller.ReturnItemDTO updateItem(@RequestBody @Valid ItemDTO.Controller.UpdateItemDTO itemDTO,
                                                       @NotNull @Positive @PathVariable Long itemId,
                                                       @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.updateItem(ItemDTO.Controller.Mapper.toItem(itemDTO), ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDTO.Controller.ReturnItemWithBookingsDTO getItem(@NotNull @Positive @PathVariable Long itemId,
                                                                @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return itemService.getItem(itemId, idOwner);
    }

    @GetMapping
    public List<ItemDTO.Controller.ReturnItemWithBookingsDTO> getAllItems(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner,
                                                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemService.getAllItems(idOwner, from, size);
    }

    @GetMapping("/search")
    public List<ItemDTO.Controller.ReturnItemDTO> searchItems(@RequestParam String text,
                                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDTO.Controller.ReturnCommentDTO addComment(@RequestBody @Valid CommentDTO.Controller.NewCommentDTO commentDTO,
                                                             @NotNull @Positive @PathVariable Long itemId,
                                                             @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return itemService.addComment(CommentDTO.Controller.Mapper.toComment(commentDTO), idOwner, itemId);
    }
}
