package ru.practicum.shareit.item.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemStore;
    private final UserService userService;
    private final BookingRepository bookingStore;
    private final CommentRepository commentRepository;
    private final RequestService requestService;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ItemServiceImpl(ItemRepository store, UserServiceImpl userService,
                           CommentRepository commentRepository, RequestService requestService, BookingRepository bookingStore) {
        this.itemStore = store;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.requestService = requestService;
        this.bookingStore = bookingStore;
    }

    @Transactional
    @Override       //idOwner from headers
    public ItemDTO.Controller.ReturnItemDTO addItem(Item item, Long idOwner, Long itemRequest) {
        item.setId(null);
        User usr = userService.getSimpleUser(idOwner);
        UserService.validate(usr);
        ItemService.validate(item);

        itemStore.save(item);   //Вроде лишнее для сохранения, но иначе не получить id нового элемента для вывода
        usr.addItem(item);
        if (itemRequest != null) {
            ItemRequest req = requestService.getSimpleItemRequest(itemRequest);
            req.addRecommendedItems(item);
        }
        return ItemDTO.Controller.Mapper.toReturnItemDTO(item, itemRequest);
    }

    @Override
    public List<ItemDTO.Controller.ReturnItemWithBookingsDTO> getAllItems(Long ownerId, Integer from, Integer size) {
        User user = userService.getSimpleUser(ownerId);
        UserService.validate(user);
        Pageable pag =
                PageRequest.of(from / size, size, Sort.by("id"));

        List<Item> items = itemStore.findItemByOwner(user, pag);
        List<Long> ids = items.stream().map(it -> it.getId()).collect(Collectors.toList());
        List<Booking> bCur = bookingStore.findNextBookings(ownerId, ids, LocalDateTime.now());
        final Map<Long, Booking> bCurMap = bCur.stream().collect(
                Collectors.toMap(b -> b.getBooked().getId(), b -> b, (a, b) -> a));
        List<Booking> bLast = bookingStore.findLastBookings(ownerId, ids, LocalDateTime.now());
        final Map<Long, Booking> bLastMap = bLast.stream().collect(
                Collectors.toMap(b -> b.getBooked().getId(), b -> b, (a, b) -> a));
        List<Comment> commentsList = commentRepository.findComments(ids, pag);
        final Map<Long, List<Comment>> bComMap = commentsList.stream().collect(
                Collectors.toMap(b -> b.getItemCommented().getId(), b -> List.of(b),
                        (a, b) -> List.of(a, b).stream().flatMap(Collection::stream).collect(Collectors.toList())));


        List<ItemDTO.Controller.ReturnItemWithBookingsDTO> rez =
                items.stream()
                        .map(item -> ItemDTO.Controller.Mapper.toReturnItemWithBookingsDTO(
                                item,
                                bLastMap.get(item.getId()),
                                bCurMap.get(item.getId()),
                                bComMap.get(item.getId())))
                        .collect(Collectors.toList());

        return rez;

    }

    public Item getSimpleItem(Long id) {
        Item it = itemStore.findItemById(id).orElseThrow(() ->
                new HttpCustomException(HttpStatus.NOT_FOUND, "Item with id=" + id + " is absent")
        );
        return it;
    }

    @Override
    public ItemDTO.Controller.ReturnItemWithBookingsDTO getItem(Long itemId, Long ownerId) {
        Item it = getSimpleItem(itemId);
        ItemService.validate(it);
        User user = userService.getSimpleUser(ownerId);
        UserService.validate(user);

        Pageable pag = PageRequest.of(0, 1);
        List<Booking> lst = null;
        lst = bookingStore.findLastBookings(ownerId, List.of(itemId), LocalDateTime.now());
        Booking lastBooking = lst.isEmpty() ? null : lst.get(0);
        lst = bookingStore.findNextBookings(ownerId, List.of(itemId), LocalDateTime.now());
        Booking nextBooking = lst.isEmpty() ? null : lst.get(0);
        List<Comment> com = commentRepository.findComments(List.of(itemId), PageRequest.of(0, 10));

        return ItemDTO.Controller.Mapper.toReturnItemWithBookingsDTO(it, lastBooking, nextBooking, com);
    }

    @Transactional
    @Override
    public ItemDTO.Controller.ReturnItemDTO updateItem(Item item, Long ownerId, Long itemId) {
        Item it = getSimpleItem(itemId);
        if (!it.getOwner().getId().equals(ownerId))
            throw new HttpCustomException(HttpStatus.NOT_FOUND, "Wrong user id");
        if (item.getAvailable() != null) it.setAvailable(item.getAvailable());
        if (item.getDescription() != null) it.setDescription(item.getDescription());
        if (item.getName() != null) it.setName(item.getName());
        return ItemDTO.Controller.Mapper.toReturnItemDTO(it);
    }

    @Override
    public List<ItemDTO.Controller.ReturnItemDTO> searchItems(String text, Integer from, Integer size) {
        if (text.isBlank()) return List.of();
        Pageable pag = PageRequest.of(from, size);
        List<ItemDTO.Controller.ReturnItemDTO> rez =
                itemStore.findByDescriptionContainsIgnoreCaseAndAvailableIsTrueOrNameContainsIgnoreCaseAndAvailableIsTrue(text, text, pag).stream()
                        .map(item -> ItemDTO.Controller.Mapper.toReturnItemDTO(item))
                        .collect(Collectors.toList());

        return rez;
    }

    @Transactional
    @Override
    public CommentDTO.Controller.ReturnCommentDTO addComment(Comment comment, Long idOwner, Long itemId) {
        User user = userService.getSimpleUser(idOwner);
        Item item = itemStore.findItemById(itemId).orElse(null);
        UserService.validate(user);
        ItemService.validate(item);
        ItemService.validateComment(comment);

        if (!userService.isEarlierItemBookedByUser(itemId, idOwner))
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "The user has never booked the item");

        comment.setItemCommented(item);
        comment.setAuthorName(user.getName());
        commentRepository.save(comment);
        item.addComment(comment);
        return CommentDTO.Controller.Mapper.toReturnCommentDTO(comment);
    }
}
