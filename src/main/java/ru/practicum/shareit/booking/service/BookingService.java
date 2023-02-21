package ru.practicum.shareit.booking.service;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface BookingService extends BookingServiceSort {
    BookingDTO.Controller.ReturnBookItemDTO addItem(Booking item, Long bookerId, Long itemId);
    BookingDTO.Controller.ReturnBookItemDTO setApprove(Long bookingId, Long bookerId, String approved);
    BookingDTO.Controller.ReturnBookItemDTO findByIdAndBookerOrIdAndOwner(Long bookingId, Long bookerId);
    List<BookingDTO.Controller.ReturnBookItemDTO> findByBooker(Long bookerId, String state);
    List<BookingDTO.Controller.ReturnBookItemDTO> findByOwner(Long bookerId, String state);

    static boolean validate(Booking booking){
        if(booking == null) throw new HttpCustomException(HttpStatus.NOT_FOUND, "Wrong booking id");
        if(booking.getStart().isAfter(booking.getEnd())
                || booking.getStart().toLocalDate().isBefore(LocalDate.now()))
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Wrong booking time interval");
        return true;
    }
}
