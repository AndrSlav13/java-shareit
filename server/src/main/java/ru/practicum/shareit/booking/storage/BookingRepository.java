package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingCriteriaRepository {
    @Query("select b from Booking as b " +
            "left join Item as i on b.booked.id = i.id " +
            "where (b.booker.id = ?2 or i.owner.id = ?2) and b.id = ?1 ")
    List<Booking> findByIdAndBookerOrIdAndOwner(Long bookingId, Long bookerId);

    @Query("select booking from Booking as booking " +
            "where booking.booked.id in :ids " +
            "and booking.booked.owner.id = :ownerId " +
            "and booking.start in ( " +
            "select max(b.start) " +
            "from Booking as b " +
            "where b.booked.id = booking.booked.id " +
            "and b.start < :time ) ")
    List<Booking> findLastBookings(@Param("ownerId") Long ownerId, @Param("ids") List<Long> ids, @Param("time") LocalDateTime time);

    @Query("select booking from Booking as booking " +
            "where booking.booked.id in :ids " +
            "and booking.booked.owner.id = :ownerId " +
            "and booking.start in ( " +
            "select min(b.start) " +
            "from Booking as b " +
            "where b.booked.id = booking.booked.id " +
            "and b.start > :time ) ")
    List<Booking> findNextBookings(@Param("ownerId") Long ownerId, @Param("ids") List<Long> ids, @Param("time") LocalDateTime time);
}
