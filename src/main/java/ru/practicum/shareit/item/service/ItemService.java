package ru.practicum.shareit.item.service;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDTO.Controller.ReturnItemDTO addItem(Item item, Long idOwner, Long itemRequest);

    List<ItemDTO.Controller.ReturnItemWithBookingsDTO> getAllItems(Long ownerId, Integer from, Integer size);

    ItemDTO.Controller.ReturnItemDTO updateItem(Item item, Long ownerId, Long itemId);

    ItemDTO.Controller.ReturnItemWithBookingsDTO getItem(Long itemId, Long idOwner);

    List<ItemDTO.Controller.ReturnItemDTO> searchItems(String text, Integer from, Integer size);

    Item getSimpleItem(Long id);

    CommentDTO.Controller.ReturnCommentDTO addComment(Comment comment, Long idOwner, Long itemId);

    static boolean validate(Item item) {
        if (item == null) throw new HttpCustomException(HttpStatus.NOT_FOUND, "The item doesn't exist");
        return true;
    }

    static boolean validateComment(Comment comment) {
        if (comment.getText().isEmpty())
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Blank comment is forbidden");
        return true;
    }
}