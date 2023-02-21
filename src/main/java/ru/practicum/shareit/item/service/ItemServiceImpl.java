package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemStore;
    private final UserService userService;
    private CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository store, UserServiceImpl userService, CommentRepository commentRepository) {
        this.itemStore = store;
        this.userService = userService;
        this.commentRepository = commentRepository;
    }

    @Override       //idOwner from headers
    public ItemDTO.Controller.ReturnItemDTO addItem(Item item, Long idOwner) {
        User usr = userService.getSimpleUser(idOwner);
        UserService.validate(usr);
        ItemService.validate(item);

        itemStore.save(item);   //Вроде лишнее для сохранения, но иначе не получить id нового элемента для вывода
        usr.addItem(item);
        return ItemDTO.Controller.Mapper.toReturnItemDTO(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDTO.Controller.ReturnItemWithBookingsDTO> getAllItems(Long ownerId) {
        User user = userService.getSimpleUser(ownerId);
        UserService.validate(user);

        return itemStore.findAllByOwnerId(ownerId).stream()
                .map(item -> getItem(item.getId(), Optional.of(ownerId)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Item getSimpleItem(Long id){
        Item it = itemStore.findItemById(id).orElseThrow(()->
                new HttpCustomException(HttpStatus.NOT_FOUND, "Item with id=" + id + " is absent")
        );
        return it;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDTO.Controller.ReturnItemWithBookingsDTO getItem(Long itemId, Optional<Long> idOwner) {
        Item it = getSimpleItem(itemId);
        ItemService.validate(it);
        List<Booking> lst = it.getBookings();
        List<Comment> com = it.getComments();

        if(idOwner.isPresent() && !it.getOwner().getId().equals(idOwner.get())) lst = null;
        return ItemDTO.Controller.Mapper.toReturnItemWithBookingsDTO(it, lst, com);
    }

    @Override
    public ItemDTO.Controller.ReturnItemDTO updateItem(Item item, Long ownerId, Long itemId) {
        Item it = getSimpleItem(itemId);
        if(it.getOwner().getId() != ownerId) throw new HttpCustomException(HttpStatus.NOT_FOUND, "Wrong user id");
        if(item.getAvailable() != null) it.setAvailable(item.getAvailable());
        if(item.getDescription() != null) it.setDescription(item.getDescription());
        if(item.getName() != null) it.setName(item.getName());
        return ItemDTO.Controller.Mapper.toReturnItemDTO(it);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDTO.Controller.ReturnItemDTO> searchItems(String text) {
        if(text.isBlank()) return List.of();
        return itemStore.findByDescriptionContainsIgnoreCaseAndAvailableIsTrueOrNameContainsIgnoreCaseAndAvailableIsTrue(text, text).stream()
                .map(item -> ItemDTO.Controller.Mapper.toReturnItemDTO(item))
                .collect(Collectors.toList());
    }
    @Override
    public
    CommentDTO.Controller.ReturnCommentDTO addComment(Comment comment, Long idOwner, Long itemId){
        User user = userService.getSimpleUser(idOwner);
        Item item = itemStore.findItemById(itemId).orElse(null);
        UserService.validate(user);
        ItemService.validate(item);
        ItemService.validateComment(comment);

        if(!userService.isEarlierItemBookedByUser(itemId, idOwner))
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "The user has never booked the item");

        comment.setItemCommented(item);
        comment.setAuthorName(user.getName());

        return CommentDTO.Controller.Mapper.toReturnCommentDTO(commentRepository.save(comment));
    }
}
