package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * TODO Sprint add-bookings.
 */
@Builder
@Data
public class Booking {
    private Integer id;
    private Instant start;
    private Instant end;
    private Integer itemId;
    private Integer bookerId;
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;
        if (this == o || this.getId().equals(((Booking) o).getId())) return true;
        if (start.isAfter(((Booking) o).getStart()) && start.isBefore(((Booking) o).getEnd()) ||
                end.isAfter(((Booking) o).getStart()) && end.isBefore(((Booking) o).getEnd()) ||
                start.isBefore(((Booking) o).getStart()) && end.isAfter(((Booking) o).getEnd())
        ) return true;  //Есть пересечение
        return false;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}