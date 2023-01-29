package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class ItemStoreImpl implements ItemStore {
    private static final AtomicInteger count = new AtomicInteger(1);  //Счетчик id
    private HashMap<Integer, ItemDTO.Database.DBItemDTO> itemStore;

    public ItemStoreImpl() {
        itemStore = new HashMap<>();
    }

    @Override
    public Item addItem(ItemDTO.Database.DBItemDTO itemDB) {
        Integer cnt = count.getAndIncrement();
        itemDB.setId(cnt);
        itemStore.put(cnt, itemDB);
        return ItemDTO.Database.Mapper.toItem(itemDB);
    }

    @Override
    public List<Item> getAllItems(Integer ownerId) {
        return itemStore.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .map(itemDB -> ItemDTO.Database.Mapper.toItem(itemDB))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Integer id) {
        return ItemDTO.Database.Mapper.toItem(itemStore.get(id));
    }

    @Override
    public Item updateItem(ItemDTO.Database.DBItemDTO item) {
        ItemDTO.Database.DBItemDTO it = itemStore.get(item.getId());
        if (item.getName() != null) it.setName(item.getName());
        if (item.getDescription() != null) it.setDescription(item.getDescription());
        if (item.getAvailable() != null) it.setAvailable(item.getAvailable());
        return getItemById(item.getId());
    }

    @Override
    public Item getItem(Integer itemId) {
        return ItemDTO.Database.Mapper.toItem(itemStore.get(itemId));
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isEmpty()) return List.of();
        return itemStore.values().stream()
                .filter((item) -> {
                    return (item.getDescription().toLowerCase().contains(text.toLowerCase())
                            || item.getName().toLowerCase().contains(text.toLowerCase())) && item.getAvailable();
                })
                .map(item -> ItemDTO.Database.Mapper.toItem(item))
                .collect(Collectors.toList());
    }
}
