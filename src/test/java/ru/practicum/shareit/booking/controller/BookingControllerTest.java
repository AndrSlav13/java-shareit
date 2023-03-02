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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void setApprovedTest() throws Exception {
        Mockito
                .when(bookingService.setApprove(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    User u = User.builder().id((Long) (i.getArguments()[1])).build();
                    String s = (String) (i.getArguments()[2]);
                    Item b = Item.builder().owner(u).available(Boolean.valueOf(s)).build();
                    booking = booking.toBuilder().booked(b).booker(u).id((Long) (i.getArguments()[0])).build();
                    return BookingDTO.Controller.Mapper.toReturnBookItemDTO(booking);
                });

        mvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .param("approved", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(booking.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.booker.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.available").value(false));
    }

    @Test
    void getBookingsOfOwnerTest() throws Exception {
        Mockito
                .when(bookingService.findByOwner(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    User u = User.builder().id((Long) (i.getArguments()[0])).build();
                    String s = (String) (i.getArguments()[1]);
                    Item b = Item.builder().owner(u).build();
                    booking = booking.toBuilder().booked(b).booker(u).build();
                    return List.of(BookingDTO.Controller.Mapper.toReturnBookItemDTO(booking));
                });

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(booking.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.id").value(user.getId()));
    }


    String userEmail = "qwerty@yandex.ru";
    String userName = "John";
    String itemName = "Щетка";
    String itemDescription = "something";
    Boolean itemAvailable = true;
    BookingStatus bookingStatus = BookingStatus.WAITING;
    LocalDateTime bookingStart = LocalDateTime.now().plusDays(2);
    LocalDateTime bookingEnd = LocalDateTime.now().plusDays(4);
    Long userId = 2L;
    Long itemId = 25L;
    Long bookingId = 13L;

    User user = null;
    Item item = null;
    Booking booking = null;


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
        booking = Booking.builder()
                .id(bookingId)
                .booked(item)
                .start(bookingStart)
                .end(bookingEnd)
                .status(bookingStatus)
                .booker(user)
                .build();

    }

}