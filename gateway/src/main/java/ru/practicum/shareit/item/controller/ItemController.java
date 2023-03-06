package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemDTO.Controller.NewItemDTO itemDTO,
                                          @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return itemClient.addItem(idOwner, itemDTO);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @Valid ItemDTO.Controller.UpdateItemDTO itemDTO,
                                                       @NotNull @Positive @PathVariable Long itemId,
                                                       @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.updateItem(itemDTO, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@NotNull @Positive @PathVariable Long itemId,
                                                                @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return itemClient.getItem(idOwner, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner,
                                                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemClient.getAllItems(idOwner, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        ResponseEntity<Object> ee = itemClient.searchItems(0L, text, from, size);
        return itemClient.searchItems(0L, text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Valid CommentDTO.Controller.NewCommentDTO commentDTO,
                                                             @NotNull @Positive @PathVariable Long itemId,
                                                             @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return itemClient.addComment(commentDTO, idOwner, itemId);
    }
}
