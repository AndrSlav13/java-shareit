package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTestIntegral {

    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingService bookingService;

    @PersistenceContext
    private EntityManager em;

    @Test
    void addItem() {
        userId = userService.addUser(user).getId();
        itemId = itemService.addItem(item, userId, null).getId();
        ItemDTO.Controller.ReturnItemDTO it = itemService.getItem(itemId, userId);

        assertThat(it).isNotNull();
        assertThat(it.getId()).isEqualTo(itemId);
        assertThat(it.getOwnerId()).isEqualTo(userId);
    }

    @Test
    void getAllItems_areAvailableForOwnerNotBooker() {
        userId = userService.addUser(user).getId();
        userId2 = userService.addUser(user2).getId();
        itemId = itemService.addItem(item, userId, null).getId();
        itemId2 = itemService.addItem(item2, userId, null).getId();
        List<ItemDTO.Controller.ReturnItemWithBookingsDTO> it = itemService.getAllItems(userId, 0, 4);

        assertThat(it).isNotNull();
        assertThat(it).isNotEmpty().hasSize(2);
        assertThat(it.get(0).getId()).isEqualTo(userId);
        HttpCustomException e = assertThrows(HttpCustomException.class, () -> itemService.getAllItems(10L, 0, 4));
        assertThat(e.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

        it = itemService.getAllItems(userId2, 0, 4);
        assertThat(it).isEmpty();

        bookingService.addItem(booking = booking.toBuilder()
                        .booked(item)
                        .booker(user2)
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .build(),
                userId2, itemId);
        bookingService.addItem(booking2 = booking2.toBuilder()
                        .booked(item2)
                        .booker(user2)
                        .start(LocalDateTime.now().plusDays(3))
                        .end(LocalDateTime.now().plusDays(4))
                        .build(),
                userId2, itemId2);
        it = itemService.getAllItems(userId2, 0, 4);
        assertThat(it).isEmpty();
    }

    @Test
    void getSimpleItem() {
        userId = userService.addUser(user).getId();
        itemId = itemService.addItem(item, userId, null).getId();
        Item it = itemService.getSimpleItem(itemId);

        assertThat(it).isNotNull();
        assertThat(it.getId()).isEqualTo(itemId);
        assertThat(it.getOwner().getName()).isEqualTo(userName);
    }

    @Test
    void getItem_availableForUserNotBooker() {
        userId = userService.addUser(user).getId();
        userId2 = userService.addUser(user2).getId();
        itemId = itemService.addItem(item, userId, null).getId();
        ItemDTO.Controller.ReturnItemWithBookingsDTO it = itemService.getItem(itemId, userId);

        assertThat(it.getId()).isEqualTo(itemId);
        assertThat(it.getName()).isEqualTo(item.getName());

        it = itemService.getItem(itemId, userId2);
        assertThat(it.getId()).isEqualTo(itemId);

        bookingId = bookingService.addItem(booking = booking.toBuilder()
                        .booked(item)
                        .booker(user2)
                        .start(LocalDateTime.now().plusSeconds(1))
                        .end(LocalDateTime.now().plusSeconds(2))
                        .build(),
                userId2, itemId).getId();

        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        it = itemService.getItem(itemId, userId);
        itemService.addComment(comment, userId2, itemId);
        assertThat(it.getLastBooking()).isNotNull();
        assertThat(it.getLastBooking().getId()).isEqualTo(bookingId);
        it = itemService.getItem(itemId, userId2);
        assertThat(it.getLastBooking()).isNull();
        HttpCustomException e = assertThrows(HttpCustomException.class, () -> itemService.addComment(comment, userId, itemId));
        assertThat(e.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void updateItem() {
        userId = userService.addUser(user).getId();
        itemId = itemService.addItem(item, userId, null).getId();
        itemService.updateItem(item2, userId, itemId);
        Item it = itemService.getSimpleItem(itemId);

        assertThat(it).isNotNull();
        assertThat(it.getDescription()).isEqualTo(item2.getDescription());
        assertThat(it.getName()).isEqualTo(item2.getName());
    }

    @Test
    void searchItems() {
        userId = userService.addUser(user).getId();
        itemId = itemService.addItem(item, userId, null).getId();
        itemId2 = itemService.addItem(item2, userId, null).getId();
        List<ItemDTO.Controller.ReturnItemDTO> it = itemService.searchItems("кУр", 0, 10);

        assertThat(it).isNotEmpty().hasSize(1);
        assertThat(it.get(0).getId()).isEqualTo(item2.getId());
    }

    String userEmail = "qwerty@yandex.ru";
    String userName = "John";
    String itemName = "Щетка";
    String itemDescription = "something";
    Boolean itemAvailable = true;
    String userEmail2 = "qwerty@google.ru";
    String userName2 = "Sam";
    String itemName2 = "Куртка";
    String itemDescription2 = "nothing";
    Boolean itemAvailable2 = true;
    BookingStatus bookingStatus = BookingStatus.WAITING;
    LocalDateTime bookingStart = LocalDateTime.now().plusDays(2);
    LocalDateTime bookingEnd = LocalDateTime.now().plusDays(4);
    BookingStatus bookingStatus2 = BookingStatus.APPROVED;
    LocalDateTime bookingStart2 = LocalDateTime.now().plusDays(20);
    LocalDateTime bookingEnd2 = LocalDateTime.now().plusDays(40);
    Long userId = 2L;
    Long userId2 = 11L;
    Long itemId = 25L;
    Long itemId2 = 7L;
    Long bookingId = 13L;
    Long bookingId2 = 14L;

    Long requestId = 88L;
    String requestDescription = "req desc";
    LocalDateTime requestCreated = LocalDateTime.now();
    Long requestId2 = 886L;
    String requestDescription2 = "req2 desc";
    LocalDateTime requestCreated2 = LocalDateTime.now().minusDays(8);
    String commentText = "comment text";
    Long commentId = 77L;
    LocalDateTime commentCreated = LocalDateTime.now().minusDays(22);
    String commentText2 = "comment text2";
    Long commentId2 = 771L;
    LocalDateTime commentCreated2 = LocalDateTime.now().minusDays(220);

    User user = null;
    User user2 = null;
    Item item = null;
    Item item2 = null;
    Booking booking = null;
    Booking booking2 = null;

    ItemRequest request = null;
    ItemRequest request2 = null;
    Comment comment = null;
    Comment comment2 = null;


    @BeforeEach
    public void before() {
        user = User.builder()
                .id(userId)
                .items(new ArrayList<>())
                .bookings(new ArrayList<>())
                .requests(new ArrayList<>())
                .email(userEmail)
                .name(userName)
                .build();
        user2 = User.builder()
                .id(userId2)
                .items(new ArrayList<>())
                .bookings(new ArrayList<>())
                .requests(new ArrayList<>())
                .email(userEmail2)
                .name(userName2)
                .build();
        item = Item.builder()
                .id(itemId)
                .requests(new ArrayList<>())
                .bookings(new ArrayList<>())
                .comments(new ArrayList<>())
                .owner(user)
                .description(itemDescription)
                .name(itemName)
                .available(itemAvailable)
                .build();
        item2 = Item.builder()
                .id(itemId2)
                .requests(new ArrayList<>())
                .bookings(new ArrayList<>())
                .comments(new ArrayList<>())
                .owner(user2)
                .description(itemDescription2)
                .name(itemName2)
                .available(itemAvailable2)
                .build();
        booking = Booking.builder()
                .id(bookingId)
                .booked(item)
                .start(bookingStart)
                .end(bookingEnd)
                .status(bookingStatus)
                .booker(user)
                .build();
        booking2 = Booking.builder()
                .id(bookingId2)
                .booked(item)
                .start(bookingStart)
                .end(bookingEnd)
                .status(bookingStatus)
                .booker(user)
                .build();
        request = ItemRequest.builder()
                .items(new ArrayList<>())
                .requestor(user)
                .created(requestCreated)
                .id(requestId)
                .description(requestDescription)
                .build();
        request2 = ItemRequest.builder()
                .items(new ArrayList<>())
                .requestor(user2)
                .created(requestCreated2)
                .id(requestId2)
                .description(requestDescription2)
                .build();
        comment = Comment.builder()
                .created(commentCreated)
                .authorName(userName)
                .itemCommented(item)
                .text(commentText)
                .id(commentId)
                .build();
        comment2 = Comment.builder()
                .created(commentCreated2)
                .authorName(userName2)
                .itemCommented(item2)
                .text(commentText2)
                .id(commentId2)
                .build();
    }

}