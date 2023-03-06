package ru.practicum.shareit.booking.controller;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exceptions.WrongEnumStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum BookingStateForOutput {
    ALL(new Container(
            List.of(BookingStatus.APPROVED, BookingStatus.REJECTED, BookingStatus.CANCELED, BookingStatus.WAITING),
            BookingTimeInterval.ALL)),
    CURRENT(new Container(
            List.of(BookingStatus.APPROVED, BookingStatus.REJECTED, BookingStatus.CANCELED, BookingStatus.WAITING),
            BookingTimeInterval.CURRENT)),
    PAST(new Container(
            List.of(BookingStatus.APPROVED, BookingStatus.REJECTED, BookingStatus.CANCELED, BookingStatus.WAITING),
            BookingTimeInterval.PAST)),
    FUTURE(new Container(
            List.of(BookingStatus.APPROVED, BookingStatus.REJECTED, BookingStatus.CANCELED, BookingStatus.WAITING),
            BookingTimeInterval.FUTURE)),
    REJECTED(new Container(
            List.of(BookingStatus.REJECTED),
            BookingTimeInterval.ALL)),
    WAITING(new Container(
            List.of(BookingStatus.WAITING),
            BookingTimeInterval.ALL));

    public static class Container {
        public List<BookingStatus> bookingStatuses;
        public BookingTimeInterval bookingTimeInterval;

        public Container(List<BookingStatus> bookingStatuses, BookingTimeInterval bookingTimeInterval) {
            this.bookingStatuses = bookingStatuses;
            this.bookingTimeInterval = bookingTimeInterval;
        }
    }

    public final Container label;

    public static BookingStateForOutput vOf(String str) {
        if (!Arrays.stream(BookingStateForOutput.values()).map(v -> v.name()).collect(Collectors.toList()).contains(str))
            throw new WrongEnumStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown state: UNSUPPORTED_STATUS");
        return valueOf(str);
    }

    @Override
    public String toString() {
        return this.name();
    }

    BookingStateForOutput(Container label) {
        this.label = label;
    }
}