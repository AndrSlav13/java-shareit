package ru.practicum.shareit.user.storage;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.List;

public class UserCriteriaRepositoryImpl implements UserCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean isEarlierItemBookedByUser(Long itemId, Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createTupleQuery();
        Root<User> root = cr.from(User.class);
        Join<User, List<Booking>> joinItems = root.join("bookings", JoinType.LEFT);

        Path<Long> uId = root.get("id");
        Path<LocalDateTime> dateBooked = joinItems.get("start");
        Path<Item> itemBooked = joinItems.get("booked");
        Path<Long> iId = itemBooked.get("id");

        cr.multiselect(joinItems);

        Predicate userPredicat = cb.equal(uId, cb.literal(userId));
        Predicate datePredicat = cb.lessThanOrEqualTo(dateBooked, cb.currentTimestamp().as(LocalDateTime.class));
        Predicate itemPredicat = cb.equal(iId, cb.literal(itemId));
        List<Tuple> tuples = entityManager.createQuery(cr.where(cb.and(userPredicat, itemPredicat, datePredicat))).getResultList();

        return tuples.isEmpty() ? false : true;
    }

    @Override
    public boolean isUserBooker(Long itemId, Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createTupleQuery();
        Root<User> root = cr.from(User.class);
        Join<User, List<Booking>> joinItems = root.join("bookings", JoinType.LEFT);

        Path<Long> uId = root.get("id");
        Path<Item> itemBooked = joinItems.get("booked");
        Path<Long> iId = itemBooked.get("id");

        cr.multiselect(joinItems);

        Predicate userPredicat = cb.equal(uId, cb.literal(userId));
        Predicate itemPredicat = cb.equal(iId, cb.literal(itemId));
        List<Tuple> tuples = entityManager.createQuery(cr.where(cb.and(userPredicat, itemPredicat))).getResultList();

        return tuples.isEmpty() ? false : true;
    }

    @Override
    public boolean isUserOwner(Long itemId, Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createTupleQuery();
        Root<User> root = cr.from(User.class);
        Join<User, List<Item>> joinItems = root.join("items", JoinType.LEFT);

        Path<Long> uId = root.get("id");
        Path<Long> iId = joinItems.get("id");

        cr.multiselect(joinItems);

        Predicate userPredicat = cb.equal(uId, cb.literal(userId));
        Predicate itemPredicat = cb.equal(iId, cb.literal(itemId));
        List<Tuple> tuples = entityManager.createQuery(cr.where(cb.and(userPredicat, itemPredicat))).getResultList();

        return tuples.isEmpty() ? false : true;
    }

    @Override
    public boolean isUserOwnerOrBooker(Long itemId, Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createTupleQuery();
        Root<User> root = cr.from(User.class);
        Join<User, List<Booking>> joinItems = root.join("bookings", JoinType.LEFT);
        Join<User, List<Item>> joinItems2 = root.join("items", JoinType.LEFT);

        Path<Long> uId = root.get("id");
        Path<Item> itemBooked = joinItems.get("booked");
        Path<Long> iId = itemBooked.get("id");      //book
        Path<Long> oId = joinItems2.get("id");       //own

        cr.multiselect(joinItems);

        Predicate userPredicat = cb.equal(uId, cb.literal(userId));
        Predicate itemBookPredicat = cb.equal(iId, cb.literal(itemId));
        Predicate itemOwnPredicat = cb.equal(oId, cb.literal(itemId));
        List<Tuple> tuples = entityManager.createQuery(cr.where(cb.and(userPredicat, cb.or(itemOwnPredicat, itemBookPredicat)))).getResultList();

        return tuples.isEmpty() ? false : true;
    }

    @Override
    public boolean isUserAbleToBook(Long itemId, Long userId) {

        if (isUserOwner(itemId, userId))
            throw new HttpCustomException(HttpStatus.NOT_FOUND, "The user is already owner of the item");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cr = cb.createTupleQuery();
        Root<Item> root = cr.from(Item.class);

        Path<Long> iId = root.get("id");
        Path<Boolean> available = root.get("available");

        cr.multiselect(root);

        Predicate itemPredicat = cb.equal(iId, cb.literal(itemId));
        Predicate avaPredicat = cb.equal(available, cb.literal(true));
        List<Tuple> tuples = entityManager.createQuery(cr.where(cb.and(itemPredicat, avaPredicat))).getResultList();

        if (tuples.isEmpty())
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "The item is not available");

        return true;
    }
}
