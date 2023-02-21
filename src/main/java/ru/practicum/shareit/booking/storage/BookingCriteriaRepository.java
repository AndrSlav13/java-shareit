package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateForOutput;

import java.util.List;

public interface BookingCriteriaRepository {
    List<Booking> findByBooker(Long bookerId, BookingStateForOutput state);

    List<Booking> findByOwner(Long ownerId, BookingStateForOutput state);
}
