package ru.practicum.shareit.booking.storage;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateForOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BookingCriteriaRepositoryImpl implements BookingCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private Predicate predicatBooker(CriteriaBuilder cb, BookingStateForOutput state,
                                     Path<BookingStatus> us, Path<LocalDateTime> start, Path<LocalDateTime> end) {
        Predicate predicateStatus = cb.equal(us, cb.literal(state.label.bookingStatuses.get(0)));
        Predicate predicateTime = null;

        for (BookingStatus b : state.label.bookingStatuses)
            predicateStatus = cb.or(predicateStatus, cb.equal(us, cb.literal(b)));

        switch (state.label.bookingTimeInterval) {
            case ALL:
                predicateTime = cb.equal(cb.literal(1), cb.literal(1));
                break;
            case PAST:
                predicateTime = cb.lessThanOrEqualTo(end.as(LocalDateTime.class), LocalDateTime.now());
                break;
            case CURRENT:
                predicateTime = cb.and(cb.lessThanOrEqualTo(start.as(LocalDateTime.class), LocalDateTime.now()),
                        cb.greaterThanOrEqualTo(end.as(LocalDateTime.class), LocalDateTime.now()));
                break;
            case FUTURE:
                predicateTime = cb.greaterThanOrEqualTo(start.as(LocalDateTime.class), LocalDateTime.now());
                break;
            default:
                throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Wrong timelaps definition for bookings - ");
        }
        return cb.and(predicateStatus, predicateTime);
    }

    public
    @Override
    List<Booking> findByBooker(Long bookerId, BookingStateForOutput state, Integer from, Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createTupleQuery();

        Root<User> root = cr.from(User.class);
        Join<User, List<Booking>> joinItems = root.join("bookings");

        Path<Long> id = root.get("id");
        Path<BookingStatus> us = joinItems.get("status");
        Path<LocalDateTime> start = joinItems.get("start");
        Path<LocalDateTime> end = joinItems.get("end");

        cr.multiselect(joinItems);

        Predicate bookingPredicat = predicatBooker(cb, state, us, start, end);
        Predicate userPredicat = cb.equal(id, cb.literal(bookerId));
        cr.orderBy(cb.desc(start));

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(cr.where(cb.and(bookingPredicat, userPredicat)));
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);
        List<Tuple> req = typedQuery.getResultList();


        return req.stream().map(tuple -> (Booking) tuple.get(joinItems))
                .sorted(BookingService.comparator)
                .collect(Collectors.toList());
    }

    public
    @Override
    List<Booking> findByOwner(Long ownerId, BookingStateForOutput state, Integer from, Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createTupleQuery();
        Root<Item> root = cr.from(Item.class);
        Join<Item, List<Booking>> joinBookings = root.join("bookings");
        Join<Item, User> joinUser = root.join("owner");

        Path<Long> id = joinUser.get("id");
        Path<BookingStatus> us = joinBookings.get("status");
        Path<LocalDateTime> start = joinBookings.get("start");
        Path<LocalDateTime> end = joinBookings.get("end");

        cr.multiselect(joinBookings);

        Predicate bookingPredicat = predicatBooker(cb, state, us, start, end);
        Predicate userPredicat = cb.equal(id, cb.literal(ownerId));
        cr.orderBy(cb.desc(start));

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(cr.where(cb.and(bookingPredicat, userPredicat)));
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);
        List<Tuple> req = typedQuery.getResultList();

        return req.stream().map(tuple -> (Booking) tuple.get(joinBookings))
                .sorted(BookingService.comparator)
                .collect(Collectors.toList());
    }
}
