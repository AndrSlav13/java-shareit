package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * TODO Sprint add-bookings.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "booking_start")
    private LocalDateTime start;
    @Column(name = "booking_end")
    private LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booked_id")
    private Item booked;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.WAITING;

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;
        if (this == o || this.getId().equals(((Booking) o).getId())) return true;
        if (start.isAfter(((Booking) o).getStart()) && start.isBefore(((Booking) o).getEnd()) ||
                end.isAfter(((Booking) o).getStart()) && end.isBefore(((Booking) o).getEnd()) ||
                start.isBefore(((Booking) o).getStart()) && end.isAfter(((Booking) o).getEnd())
        ) throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Booking is not available within this interval");  //Есть пересечение

        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}