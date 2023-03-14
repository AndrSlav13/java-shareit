package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDTO;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    BookingClient bookingClient;
    private BookingDTO.Controller.NewBookItemDTO bookDTO;


    @Test
    void addBookingException() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        LocalDateTime bookingStart = LocalDateTime.now().plusHours(2);
        LocalDateTime bookingEnd = LocalDateTime.now().plusHours(20);
        bookDTO = BookingDTO.Controller.NewBookItemDTO.builder()
                .itemId(itemId)
                .end(bookingStart.format(BookingDTO.format))
                .start(bookingEnd.format(BookingDTO.format))
                .status(BookingStatus.WAITING.toString())
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());

        bookDTO = BookingDTO.Controller.NewBookItemDTO.builder()
                .itemId(itemId)
                .end(bookingEnd.format(BookingDTO.format))
                .start(bookingStart.format(BookingDTO.format))
                .status("")
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());
    }

}