package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDTO.Controller.ReturnItemDTO addItem(@RequestBody ItemDTO.Controller.NewItemDTO itemDTO,
                                                    @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return itemService.addItem(ItemDTO.Controller.Mapper.toItem(itemDTO), idOwner, itemDTO.getRequestId());
    }


    @PatchMapping("/{itemId}")
    public ItemDTO.Controller.ReturnItemDTO updateItem(@RequestBody ItemDTO.Controller.UpdateItemDTO itemDTO,
                                                       @PathVariable Long itemId,
                                                       @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.updateItem(ItemDTO.Controller.Mapper.toItem(itemDTO), ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDTO.Controller.ReturnItemWithBookingsDTO getItem(@PathVariable Long itemId,
                                                                @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return itemService.getItem(itemId, idOwner);
    }

    @GetMapping
    public List<ItemDTO.Controller.ReturnItemWithBookingsDTO> getAllItems(@RequestHeader("X-Sharer-User-Id") Long idOwner,
                                                                          @RequestParam(defaultValue = "0") Integer from,
                                                                          @RequestParam(defaultValue = "10") Integer size) {
        return itemService.getAllItems(idOwner, from, size);
    }

    @GetMapping("/search")
    public List<ItemDTO.Controller.ReturnItemDTO> searchItems(@RequestParam String text,
                                                              @RequestParam(defaultValue = "0") Integer from,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        List<ItemDTO.Controller.ReturnItemDTO> tt = itemService.searchItems(text, from, size);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDTO.Controller.ReturnCommentDTO addComment(@RequestBody CommentDTO.Controller.NewCommentDTO commentDTO,
                                                             @PathVariable Long itemId,
                                                             @RequestHeader("X-Sharer-User-Id") Long idOwner) {
        return itemService.addComment(CommentDTO.Controller.Mapper.toComment(commentDTO), idOwner, itemId);
    }
}
