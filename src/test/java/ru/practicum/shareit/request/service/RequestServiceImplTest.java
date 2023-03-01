package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestRepository;
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
class RequestServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestServiceImpl requestService;


    @Test
    void addRequest() {
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().requests(new ArrayList<>()).build());
        Mockito
                .when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(request.toBuilder().requestor(null).build());
        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        ItemRequestDTO.Controller.ReturnItemRequestDTO item1 = requestService.addRequest(request, userId);

        assertEquals(null, item1.getId());
        userMockStatic.verify(() -> UserService.validate(any(User.class)), Mockito.times(1));
        Mockito.verify(requestRepository, Mockito.times(1)).save(any(ItemRequest.class));
        assertEquals(user.getId(), item1.getRequestorId());
        userMockStatic.close();
    }

    @Test
    void getSimpleItemRequest() {
        Mockito
                .when(requestRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(request));
        ItemRequest it = requestService.getSimpleItemRequest(requestId);

        assertEquals(requestId, it.getId());

        Mockito
                .when(requestRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        HttpCustomException ex = assertThrows(HttpCustomException.class,
                () -> requestService.getSimpleItemRequest(requestId));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getCode());
    }

    @Test
    void getItemRequest() {
        Mockito
                .when(requestRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(request));
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().items(new ArrayList<>()).build());
        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        ItemRequestDTO.Controller.ReturnItemRequestDTO rez = requestService.getItemRequest(userId, requestId);

        assertEquals(requestId, rez.getId());
        userMockStatic.verify(() -> UserService.validate(any(User.class)), Mockito.times(1));
        userMockStatic.close();
    }

    @Test
    void getRequestsByRequestorId() {
        Mockito
                .when(requestRepository.findAllByRequestorId(Mockito.any(Long.class)))
                .thenReturn(List.of(request));
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().items(new ArrayList<>()).build());
        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        List<ItemRequestDTO.Controller.ReturnItemRequestDTO> rez = requestService.getRequestsByRequestorId(Optional.of(userId));

        assertEquals(requestId, rez.get(0).getId());
        userMockStatic.verify(() -> UserService.validate(any(User.class)), Mockito.times(1));
        userMockStatic.close();
    }

    @Test
    void getRequestsByNotRequestorId() {
        Mockito
                .when(requestRepository.findAllByNotRequestorId(Mockito.any(Long.class), Mockito.any(Integer.class), Mockito.any(Integer.class)))
                .thenReturn(List.of(request));
        List<ItemRequestDTO.Controller.ReturnItemRequestDTO> rez = requestService.getRequestsByNotRequestorId(Optional.of(userId), 0, 1);

        assertEquals(requestId, rez.get(0).getId());
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