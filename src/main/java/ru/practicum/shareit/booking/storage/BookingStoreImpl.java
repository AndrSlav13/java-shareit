package ru.practicum.shareit.booking.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.exceptions.HttpCustomException;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class BookingStoreImpl implements BookingStore {
    private static final AtomicInteger count = new AtomicInteger(1);  //Счетчик id
    private HashMap<Integer, BookingDTO.Database.DBScheduleDTO> bookingStore;

    public BookingStoreImpl() {
        bookingStore = new HashMap<>();
    }

    @Override
    public Integer addItem(BookingDTO.Database.DBScheduleDTO item) {
        Integer cnt = count.getAndIncrement();
        item.setItemId(cnt);
        if (bookingStore.values().contains(item))
            throw new HttpCustomException(this.getClass() + " - The item is in use within this interval");
        bookingStore.put(cnt, item);
        return cnt;
    }
}