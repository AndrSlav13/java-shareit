package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private RequestService requestService;

    @InjectMocks
    @Spy
    private ItemServiceImpl itemService;


    @Test
    void addItem() {
        Mockito
                .when(itemRepository.save(item))
                .thenReturn(item);
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().items(new ArrayList<>()).build());
        Mockito
                .when(requestService.getSimpleItemRequest(Mockito.anyLong()))
                .thenReturn(request.toBuilder().items(new ArrayList<>()).build());
        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        MockedStatic<ItemService> itemMockStatic = Mockito.mockStatic(ItemService.class);
        ItemDTO.Controller.ReturnItemDTO item1 = itemService.addItem(item, userId, requestId);

        assertEquals(null, item1.getId());
        assertEquals(userId, item1.getOwnerId());
        assertEquals(requestId, item1.getRequestId());
        userMockStatic.verify(() -> UserService.validate(any(User.class)), Mockito.times(1));
        itemMockStatic.verify(() -> ItemService.validate(any(Item.class)), Mockito.times(1));
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        userMockStatic.close();
        itemMockStatic.close();
    }

    @Test
    void getAllItems() {
        Mockito
                .when(itemRepository.findItemByOwner(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().items(new ArrayList<>()).build());
        Mockito
                .when(bookingRepository.findLastBookings(Mockito.anyLong(), Mockito.anyList(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of());
        Mockito
                .when(bookingRepository.findNextBookings(Mockito.anyLong(), Mockito.anyList(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of());
        Mockito
                .when(commentRepository.findComments(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(List.of());
        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        List<ItemDTO.Controller.ReturnItemWithBookingsDTO> rez = itemService.getAllItems(userId, 0, 2);

        assertEquals(itemId, rez.get(0).getId());
        assertEquals(userId, rez.get(0).getOwnerId());
        userMockStatic.verify(() -> UserService.validate(any(User.class)), Mockito.times(1));
        userMockStatic.close();
    }

    @Test
    void getSimpleItem() {
        Mockito
                .when(itemRepository.findItemById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(item));
        Item it = itemService.getSimpleItem(itemId);

        assertEquals(itemId, it.getId());

        Mockito
                .when(itemRepository.findItemById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        HttpCustomException ex = assertThrows(HttpCustomException.class,
                () -> itemService.getSimpleItem(itemId));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getCode());
    }

    @Test
    void getItem() {
        Mockito
                .doReturn(item).when(itemService).getSimpleItem(Mockito.anyLong());
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().items(new ArrayList<>()).build());
        Mockito
                .when(bookingRepository.findLastBookings(Mockito.anyLong(), Mockito.anyList(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of());
        Mockito
                .when(bookingRepository.findNextBookings(Mockito.anyLong(), Mockito.anyList(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of());
        Mockito
                .when(commentRepository.findComments(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(List.of());
        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        ItemDTO.Controller.ReturnItemWithBookingsDTO rez = itemService.getItem(itemId, userId);

        assertEquals(itemId, rez.getId());
        assertEquals(userId, rez.getOwnerId());
        userMockStatic.verify(() -> UserService.validate(any(User.class)), Mockito.times(1));
        userMockStatic.close();
    }

    @Test
    void updateItem() {
        Mockito
                .when(itemRepository.findItemById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(item));
        item2.toBuilder()
                .id(item.getId());
        ItemDTO.Controller.ReturnItemDTO rez = itemService.updateItem(item2, userId, itemId);

        assertEquals(item.getName(), item2.getName());
        assertEquals(item.getAvailable(), item2.getAvailable());
        assertEquals(item.getDescription(), item2.getDescription());

        HttpCustomException e = assertThrows(HttpCustomException.class, () -> itemService.updateItem(item2, userId2, itemId));
        assertEquals(HttpStatus.NOT_FOUND.value(), e.getCode());
    }

    @Test
    void searchItems() {
        Mockito
                .when(itemRepository.findByDescriptionContainsIgnoreCaseAndAvailableIsTrueOrNameContainsIgnoreCaseAndAvailableIsTrue(
                        Mockito.any(String.class),
                        Mockito.any(String.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));
        item2.toBuilder()
                .id(item.getId());
        List<ItemDTO.Controller.ReturnItemDTO> rez = itemService.searchItems("text", 0, 10);

        assertEquals(itemName, item.getName());

        rez = itemService.searchItems("", 0, 10);

        assertEquals(0, rez.size());
    }

    @Test
    void addComment() {
        Mockito
                .when(itemRepository.findItemById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().items(new ArrayList<>()).build());
        Mockito
                .when(userService.isEarlierItemBookedByUser(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comment);
        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        MockedStatic<ItemService> itemMockStatic = Mockito.mockStatic(ItemService.class);
        CommentDTO.Controller.ReturnCommentDTO com1 = itemService.addComment(comment, userId, itemId);

        assertEquals(comment.getText(), com1.getText());
        userMockStatic.verify(() -> UserService.validate(any(User.class)), Mockito.times(1));
        itemMockStatic.verify(() -> ItemService.validate(any(Item.class)), Mockito.times(1));
        itemMockStatic.verify(() -> ItemService.validateComment(any(Comment.class)), Mockito.times(1));
        Mockito.verify(commentRepository, Mockito.times(1)).save(any(Comment.class));
        userMockStatic.close();
        itemMockStatic.close();
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