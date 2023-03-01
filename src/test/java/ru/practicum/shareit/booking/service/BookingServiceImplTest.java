package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateForOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;


    @Test
    void addItem_checkAndReturnUser() {
        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(booking);
        Mockito
                .when(itemService.getSimpleItem(Mockito.anyLong()))
                .thenReturn(item.toBuilder().bookings(new ArrayList<>()).owner(User.builder().build()).build());
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().bookings(new ArrayList<>()).build());
        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        MockedStatic<ItemService> itemMockStatic = Mockito.mockStatic(ItemService.class);
        BookingDTO.Controller.ReturnBookItemDTO booking1 = bookingService.addItem(booking, userId, itemId);

        assertEquals(itemId, booking1.getItem().getId());
        assertEquals(userId, booking1.getBooker().getId());
        userMockStatic.verify(() -> UserService.validate(Mockito.any(User.class)), Mockito.times(1));
        itemMockStatic.verify(() -> ItemService.validate(Mockito.any(Item.class)), Mockito.times(1));
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
        HttpCustomException ex = assertThrows(HttpCustomException.class,
                () -> bookingService.addItem(booking.toBuilder()
                        .start(LocalDateTime.now().plusDays(6))
                        .end(LocalDateTime.now().plusDays(2))
                        .build(), userId, itemId));
        assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getCode());
        userMockStatic.close();
        itemMockStatic.close();
    }

    @Test
    void setApprove() {
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking.toBuilder().status(BookingStatus.WAITING).build()));
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().bookings(new ArrayList<>()).build());
        Mockito
                .when(userService.isUserOwner(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(true);
        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        MockedStatic<BookingService> bookingMockStatic = Mockito.mockStatic(BookingService.class);
        BookingDTO.Controller.ReturnBookItemDTO booking1 = bookingService.setApprove(booking.getId(), userId, "true");

        userMockStatic.verify(() -> UserService.validate(Mockito.any(User.class)), Mockito.times(1));
        bookingMockStatic.verify(() -> BookingService.validate(Mockito.any(Booking.class)), Mockito.times(1));
        assertEquals(BookingStatus.APPROVED.name(), booking1.getStatus().toString());
        userMockStatic.close();
        bookingMockStatic.close();

        HttpCustomException ex = assertThrows(HttpCustomException.class,
                () -> bookingService.setApprove(booking.getId(), userId, "false"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), ex.getCode());

        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking.toBuilder().status(BookingStatus.WAITING).build()));
        booking1 = bookingService.setApprove(booking.getId(), userId, "false");
        assertEquals(BookingStatus.REJECTED.name(), booking1.getStatus().toString());

        Mockito
                .when(userService.isUserOwner(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(false);
        ex = assertThrows(HttpCustomException.class,
                () -> bookingService.setApprove(booking.getId(), userId, "false"));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getCode());
    }

    @Test
    void findByIdAndBookerOrIdAndOwner() {
        Mockito
                .when(bookingRepository.findByIdAndBookerOrIdAndOwner(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of(booking.toBuilder().status(BookingStatus.WAITING).build()));
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking.toBuilder().status(BookingStatus.WAITING).build()));
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().bookings(new ArrayList<>()).build());
        Mockito
                .when(userService.isUserOwnerOrBooker(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(true);
        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        MockedStatic<BookingService> bookingMockStatic = Mockito.mockStatic(BookingService.class);
        BookingDTO.Controller.ReturnBookItemDTO booking1 = bookingService.findByIdAndBookerOrIdAndOwner(booking.getId(), userId);

        userMockStatic.verify(() -> UserService.validate(Mockito.any(User.class)), Mockito.times(1));
        bookingMockStatic.verify(() -> BookingService.validate(Mockito.any(Booking.class)), Mockito.times(1));
        assertEquals(booking.getId(), booking1.getId());
        userMockStatic.close();
        bookingMockStatic.close();

        Mockito
                .when(userService.isUserOwnerOrBooker(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(false);
        HttpCustomException ex = assertThrows(HttpCustomException.class,
                () -> bookingService.findByIdAndBookerOrIdAndOwner(booking.getId(), userId));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getCode());
    }

    @Test
    void findByBooker() {
        Mockito
                .when(bookingRepository.findByBooker(Mockito.anyLong(), Mockito.any(BookingStateForOutput.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(booking.toBuilder().status(BookingStatus.WAITING).build()));
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().bookings(new ArrayList<>()).build());

        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        List<BookingDTO.Controller.ReturnBookItemDTO> booking1 = bookingService.findByBooker(userId, BookingStateForOutput.CURRENT.toString(), 0, 1);

        userMockStatic.verify(() -> UserService.validate(Mockito.any(User.class)), Mockito.times(1));
        assertEquals(booking.getId(), booking1.get(0).getId());
        userMockStatic.close();

        HttpCustomException ex = assertThrows(HttpCustomException.class,
                () -> bookingService.findByBooker(userId, "", 0, 1));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getCode());
    }

    @Test
    void findByOwner() {
        Mockito
                .when(bookingRepository.findByOwner(Mockito.anyLong(), Mockito.any(BookingStateForOutput.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(booking.toBuilder().status(BookingStatus.WAITING).build()));
        Mockito
                .when(userService.getSimpleUser(Mockito.anyLong()))
                .thenReturn(user.toBuilder().bookings(new ArrayList<>()).build());

        MockedStatic<UserService> userMockStatic = Mockito.mockStatic(UserService.class);
        List<BookingDTO.Controller.ReturnBookItemDTO> booking1 = bookingService.findByOwner(userId, BookingStateForOutput.CURRENT.toString(), 0, 1);

        userMockStatic.verify(() -> UserService.validate(Mockito.any(User.class)), Mockito.times(1));
        assertEquals(booking.getId(), booking1.get(0).getId());
        userMockStatic.close();

        HttpCustomException ex = assertThrows(HttpCustomException.class,
                () -> bookingService.findByOwner(userId, "", 0, 1));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getCode());
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

    User user = null;
    User user2 = null;
    Item item = null;
    Item item2 = null;
    Booking booking = null;
    Booking booking2 = null;


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
    }
}