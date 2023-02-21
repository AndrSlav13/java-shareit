package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDTO.Controller.ReturnItemDTO addItem(@RequestBody @Valid ItemDTO.Controller.NewItemDTO itemDTO,
                                                    @RequestHeader("X-Sharer-User-Id") Optional<Long> idOwner) {
        return itemService.addItem(ItemDTO.Controller.Mapper.toItem(itemDTO), idOwner.get());
    }


    @PatchMapping("/{itemId}")
    public ItemDTO.Controller.ReturnItemDTO updateItem(@RequestBody @Valid ItemDTO.Controller.UpdateItemDTO itemDTO,
                                                       @PathVariable Long itemId,
                                                       @RequestHeader("X-Sharer-User-Id") Optional<Long> ownerId) {
        return itemService.updateItem(ItemDTO.Controller.Mapper.toItem(itemDTO), ownerId.get(), itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDTO.Controller.ReturnItemWithBookingsDTO getItem(@PathVariable Long itemId,
                                                    @RequestHeader("X-Sharer-User-Id") Optional<Long> idOwner) {
        return itemService.getItem(itemId, idOwner);
    }

    @GetMapping
    public List<ItemDTO.Controller.ReturnItemWithBookingsDTO> getAllItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> idOwner) {
        return itemService.getAllItems(idOwner.get());
    }

    @GetMapping("/search")
    public List<ItemDTO.Controller.ReturnItemDTO> searchItems(@RequestParam @NotBlank @NotEmpty @Valid String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDTO.Controller.ReturnCommentDTO addItem(@RequestBody @Valid CommentDTO.Controller.NewCommentDTO commentDTO,
                                                    @PathVariable Long itemId,
                                                    @RequestHeader("X-Sharer-User-Id") Optional<Long> idOwner) {
        return itemService.addComment(CommentDTO.Controller.Mapper.toComment(commentDTO), idOwner.get(), itemId);
    }
}
