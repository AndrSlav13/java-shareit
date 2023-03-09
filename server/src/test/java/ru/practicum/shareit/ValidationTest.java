package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationTest {
    @Test
    void tests() {
        HttpCustomException e = assertThrows(HttpCustomException.class, () -> ItemService.validate(null));
        assertThat(e.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        e = assertThrows(HttpCustomException.class, () -> ItemService.validateComment(Comment.builder().text("").build()));
        assertThat(e.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        e = assertThrows(HttpCustomException.class, () -> BookingService.validate(null));
        assertThat(e.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        e = assertThrows(HttpCustomException.class,
                () -> BookingService.validate(Booking.builder().start(LocalDateTime.now().plusDays(9))
                        .end(LocalDateTime.now().plusDays(1)).build()));
        assertThat(e.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        e = assertThrows(HttpCustomException.class, () -> UserService.validate(null));
        assertThat(e.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
