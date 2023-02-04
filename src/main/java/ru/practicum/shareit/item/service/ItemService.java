package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDTO.Controller.ReturnItemDTO addItem(Item item);

    List<ItemDTO.Controller.ReturnItemDTO> getAllItems(Integer ownerId);

    ItemDTO.Controller.ReturnItemDTO updateItem(Item item, Integer ownerId, Integer itemId);

    ItemDTO.Controller.ReturnItemDTO getItem(Integer itemId);

    List<ItemDTO.Controller.ReturnItemDTO> searchItems(String text);
}
