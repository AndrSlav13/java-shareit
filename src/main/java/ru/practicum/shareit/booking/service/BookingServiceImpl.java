package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStore;
import ru.practicum.shareit.booking.storage.BookingStoreImpl;
import ru.practicum.shareit.exceptions.HttpCustomException;

@Service
public class BookingServiceImpl implements BookingService {

    private void validate(Booking item) {
        if (item.getStart().isAfter(item.getEnd()))
            throw new HttpCustomException(this.getClass() + " - Begin is after End");
    }

    private final BookingStore store;

    public BookingServiceImpl(BookingStoreImpl store) {
        this.store = store;
    }

    public Integer addItem(Booking item) {
        return store.addItem(BookingDTO.Database.Mapper.toDBScheduleDTO(item));
    }
}
