package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface BookingServiceSort {
    Comparator<Booking> comparator = (b1, b2) -> {
        if(b1.getStart().equals(b2.getStart())) return 0;
        return b1.getStart().isBefore(b2.getStart()) ? 1 : -1;
    };

    static Booking getLastBooking(List<Booking> lst){
        if(lst == null || lst.isEmpty()) return null;
        return lst.get(0);
    }

    static Booking getNextBooking(List<Booking> lst){
        if(lst == null) return null;
        List<Booking> list = lst.stream()
                .sorted(comparator)
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        if(list.isEmpty()) return null;
        return list.get(list.size() - 1);
    }
}
