package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.dto.BookingDTO;

public interface BookingStore {
    Integer addItem(BookingDTO.Database.DBScheduleDTO item);
}
