package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingDTO.Controller.NewBookItemDTO bookDTO;


    @Test
    void addBooking() throws Exception {
        bookDTO = BookingDTO.Controller.NewBookItemDTO.builder()
                .itemId(item.getId())
                .end(bookingEnd.format(BookingDTO.format))
                .start(bookingStart.format(BookingDTO.format))
                .status(BookingStatus.WAITING.toString())
                .build();
        Mockito
                .when(bookingService.addItem(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    User u = User.builder().id((Long) (i.getArguments()[1])).build();
                    Item m = Item.builder().id((Long) (i.getArguments()[2])).owner(u).build();
                    Booking b = (Booking) (i.getArguments()[0]);
                    b = b.toBuilder().booked(m).booker(u).build();
                    return BookingDTO.Controller.Mapper.toReturnBookItemDTO(b);
                });

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.id").value(item.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.id").value(user.getId()));
    }

    @Test
    void addBookingException() throws Exception {
        bookDTO = BookingDTO.Controller.NewBookItemDTO.builder()
                .itemId(item.getId())
                .end(bookingStart.format(BookingDTO.format))
                .start(bookingEnd.format(BookingDTO.format))
                .status(BookingStatus.WAITING.toString())
                .build();
        Mockito
                .when(bookingService.addItem(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    User u = User.builder().id((Long) (i.getArguments()[1])).build();
                    Item m = Item.builder().id((Long) (i.getArguments()[2])).owner(u).build();
                    Booking b = (Booking) (i.getArguments()[0]);
                    b = b.toBuilder().booked(m).booker(u).build();
                    return BookingDTO.Controller.Mapper.toReturnBookItemDTO(b);
                });

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isBadRequest());

        bookDTO = BookingDTO.Controller.NewBookItemDTO.builder()
                .itemId(item.getId())
                .end(bookingEnd.format(BookingDTO.format))
                .start(bookingStart.format(BookingDTO.format))
                .status("")
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsOfUser() throws Exception {
        BookingDTO.Controller.ReturnBookItemDTO bookDTO = BookingDTO.Controller.ReturnBookItemDTO.builder()
                .id(booking.getId())
                .item(ItemDTO.Controller.Mapper.toReturnItemDTO(item))
                .booker(UserDTO.Controller.Mapper.toReturnUserDTO(user))
                .end(bookingEnd.format(BookingDTO.format))
                .start(bookingStart.format(BookingDTO.format))
                .status(BookingStatus.WAITING.toString())
                .build();
        Mockito
                .when(bookingService.findByBooker(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(bookDTO));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(booking.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].item.id").value(item.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].booker.id").value(user.getId()));
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