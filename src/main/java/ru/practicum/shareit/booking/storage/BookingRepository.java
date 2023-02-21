package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingCriteriaRepository {
    @Query("select new ru.practicum.shareit.booking.model.Booking(b.id, b.start, b.end, b.booked, b.booker, b.status) " +
            "from Booking as b left join Item as i on b.booked.id = i.id "+
            "where (b.booker.id = ?2 or i.owner.id = ?2) and b.id = ?1 ")
    List<Booking> findByIdAndBookerOrIdAndOwner(Long bookingId, Long bookerId);
}
