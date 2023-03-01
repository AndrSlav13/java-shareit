package ru.practicum.shareit.booking.service;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.HttpCustomException;

import java.time.LocalDate;
import java.util.List;

public interface BookingService extends BookingServiceSort {
    BookingDTO.Controller.ReturnBookItemDTO addItem(Booking item, Long bookerId, Long itemId);

    BookingDTO.Controller.ReturnBookItemDTO setApprove(Long bookingId, Long bookerId, String approved);

    BookingDTO.Controller.ReturnBookItemDTO findByIdAndBookerOrIdAndOwner(Long bookingId, Long bookerId);

    List<BookingDTO.Controller.ReturnBookItemDTO> findByBooker(Long bookerId, String state, Integer from, Integer size);

    List<BookingDTO.Controller.ReturnBookItemDTO> findByOwner(Long bookerId, String state, Integer from, Integer size);

    static boolean validate(Booking booking) {
        if (booking == null) throw new HttpCustomException(HttpStatus.NOT_FOUND, "Wrong booking id");
        if (booking.getStart().isAfter(booking.getEnd())
                || booking.getStart().toLocalDate().isBefore(LocalDate.now()))
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Wrong booking time interval");
        return true;
    }
}
