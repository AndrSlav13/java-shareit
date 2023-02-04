package ru.practicum.shareit.item.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStore;
import ru.practicum.shareit.item.storage.ItemStoreImpl;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStore itemStore;
    private final UserService userService;

    public ItemServiceImpl(ItemStoreImpl store, UserServiceImpl userService) {
        this.itemStore = store;
        this.userService = userService;
    }

    @Override       //idOwner from headers
    public ItemDTO.Controller.ReturnItemDTO addItem(Item item) {
        if (!userService.containsUserId(item.getOwnerId()))
            throw new HttpCustomException(HttpStatus.NOT_FOUND, "User id=" + item.getId() + " not found");
        return ItemDTO.Controller.Mapper.toReturnItemDTO(itemStore.addItem(ItemDTO.Database.Mapper.toDBItemDTO(item)));
    }

    @Override
    public List<ItemDTO.Controller.ReturnItemDTO> getAllItems(Integer ownerId) {
        return itemStore.getAllItems(ownerId).stream()
                .map(item -> ItemDTO.Controller.Mapper.toReturnItemDTO(item))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDTO.Controller.ReturnItemDTO updateItem(Item item, Integer ownerId, Integer itemId) {
        Item it = itemStore.getItemById(itemId);
        if (it == null)
            throw new HttpCustomException(HttpStatus.NOT_FOUND, "Item id=" + itemId + " not found");
        if (!it.getOwnerId().equals(ownerId))
            throw new HttpCustomException(HttpStatus.NOT_FOUND, "Item id=" + itemId + " is not at the User id=" + ownerId);
        item.setId(itemId);
        return ItemDTO.Controller.Mapper.toReturnItemDTO(itemStore.updateItem(ItemDTO.Database.Mapper.toDBItemDTO(item)));
    }

    @Override
    public ItemDTO.Controller.ReturnItemDTO getItem(Integer itemId) {
        return ItemDTO.Controller.Mapper.toReturnItemDTO(itemStore.getItem(itemId));
    }

    @Override
    public List<ItemDTO.Controller.ReturnItemDTO> searchItems(String text) {
        return itemStore.searchItems(text).stream()
                .map(item -> ItemDTO.Controller.Mapper.toReturnItemDTO(item))
                .collect(Collectors.toList());
    }
}
