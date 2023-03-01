package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void getSimpleUser() {
        Mockito
                .when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(user));
        User it = userService.getSimpleUser(userId);

        assertEquals(userId, it.getId());

        Mockito
                .when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        HttpCustomException ex = assertThrows(HttpCustomException.class,
                () -> userService.getSimpleUser(userId));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getCode());
    }

    @Test
    void getUserById() {
        Mockito
                .when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(user));
        UserDTO.Controller.ReturnUserDTO it = userService.getUserById(userId);

        assertEquals(userId, it.getId());

        Mockito
                .when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        HttpCustomException ex = assertThrows(HttpCustomException.class,
                () -> userService.getUserById(userId));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getCode());
    }

    @Test
    void getAllUsers() {
        Mockito
                .when(userRepository.findAllByIdNotNull())
                .thenReturn(List.of(user));
        List<UserDTO.Controller.ReturnUserDTO> it = userService.getAllUsers();

        assertEquals(userId, it.get(0).getId());
    }

    @Test
    void addUser() {
        user = user.toBuilder().id(null).build();
        Mockito
                .when(userRepository.save(user))
                .thenReturn(user);
        UserDTO.Controller.ReturnUserDTO it = userService.addUser(user);

        assertEquals(userEmail, it.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void updateUserById() {
        Mockito
                .when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(user));
        UserDTO.Controller.ReturnUserDTO it = userService.updateUserById(user2, userId);

        assertEquals(userId, it.getId());
        assertEquals(userName2, it.getName());
        assertEquals(userEmail2, it.getEmail());
    }

    @Test
    void deleteUserById() {
        Mockito
                .doNothing().when(userRepository).deleteById(Mockito.any(Long.class));
        userService.deleteUserById(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    void isUserBooker() {
        Mockito
                .doNothing().when(userRepository).deleteById(Mockito.any(Long.class));
        userService.deleteUserById(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    void isEarlierItemBookedByUser() {
        Mockito
                .when(userRepository.isEarlierItemBookedByUser(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(true);
        userService.isEarlierItemBookedByUser(itemId, userId);

        Mockito.verify(userRepository, Mockito.times(1)).isEarlierItemBookedByUser(itemId, userId);
    }

    @Test
    void isUserOwner() {
        Mockito
                .when(userRepository.isUserOwner(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(true);
        userService.isUserOwner(itemId, userId);

        Mockito.verify(userRepository, Mockito.times(1)).isUserOwner(itemId, userId);
    }

    @Test
    void isUserOwnerOrBooker() {
        Mockito
                .when(userRepository.isUserOwnerOrBooker(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(true);
        userService.isUserOwnerOrBooker(itemId, userId);

        Mockito.verify(userRepository, Mockito.times(1)).isUserOwnerOrBooker(itemId, userId);
    }

    @Test
    void isUserAbleToBook() {
        Mockito
                .when(userRepository.isUserAbleToBook(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(true);
        userService.isUserAbleToBook(itemId, userId);

        Mockito.verify(userRepository, Mockito.times(1)).isUserAbleToBook(itemId, userId);
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