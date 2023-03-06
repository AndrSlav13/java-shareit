package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Comparator;

public interface BookingServiceSort {
    Comparator<Booking> comparator = (b1, b2) -> {
        if (b1.getStart().equals(b2.getStart())) return 0;
        return b1.getStart().isBefore(b2.getStart()) ? 1 : -1;
    };
}
