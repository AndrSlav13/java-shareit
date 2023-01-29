package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStore {
    Item addItem(ItemDTO.Database.DBItemDTO itemDB);

    List<Item> getAllItems(Integer ownerId);

    Item getItemById(Integer id);

    Item updateItem(ItemDTO.Database.DBItemDTO item);

    Item getItem(Integer itemId);

    List<Item> searchItems(String text);
}
