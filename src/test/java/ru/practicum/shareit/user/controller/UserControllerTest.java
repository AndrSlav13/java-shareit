package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDTO.Controller.NewUserDTO userDTO;

    @Test
    void addUserTest() throws Exception {
        userDTO = UserDTO.Controller.NewUserDTO.newUserDTOBuilder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
        Mockito
                .when(userService.addUser(Mockito.any()))
                .thenAnswer(i -> {
                    User b = (User) (i.getArguments()[0]);
                    return UserDTO.Controller.Mapper.toReturnUserDTO(b);
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void addUserTestException() throws Exception {
        userDTO = UserDTO.Controller.NewUserDTO.newUserDTOBuilder()
                .name("")
                .email(user.getEmail())
                .build();
        Mockito
                .when(userService.addUser(Mockito.any()))
                .thenAnswer(i -> {
                    User b = (User) (i.getArguments()[0]);
                    return UserDTO.Controller.Mapper.toReturnUserDTO(b);
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isBadRequest());
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
