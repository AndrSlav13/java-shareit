package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDTO.Controller.ReturnItemDTO addItem(@RequestBody @Valid ItemDTO.Controller.NewItemDTO itemDTO,
                                                    @RequestHeader("X-Sharer-User-Id") Optional<Integer> idOwner) {
        return itemService.addItem(ItemDTO.Controller.Mapper.toItem(itemDTO, idOwner.get()));
    }

    @PatchMapping("/{itemId}")
    public ItemDTO.Controller.ReturnItemDTO updateItem(@RequestBody @Valid ItemDTO.Controller.UpdateItemDTO itemDTO,
                                                       @PathVariable Integer itemId,
                                                       @RequestHeader("X-Sharer-User-Id") Optional<Integer> ownerId) {
        return itemService.updateItem(ItemDTO.Controller.Mapper.toItem(itemDTO), ownerId.get(), itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDTO.Controller.ReturnItemDTO getItem(@PathVariable Integer itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDTO.Controller.ReturnItemDTO> getAllItems(@RequestHeader("X-Sharer-User-Id") Optional<Integer> idOwner) {
        return itemService.getAllItems(idOwner.get());
    }

    @GetMapping("/search")
    public List<ItemDTO.Controller.ReturnItemDTO> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
