package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureTestDatabase
@DataJpaTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findById() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();

        String query = "select user from User as user " +
                "where user.id = :id ";
        List<User> b = em.getEntityManager().createQuery(
                        query, User.class)
                .setParameter("id", user.getId())
                .getResultList();
        assertThat(b).isEmpty();

        user = userRepository.save(user);
        b = userRepository.findAll();
        User m = userRepository.findById(user.getId()).get();
        b = em.getEntityManager().createQuery(
                        query, User.class)
                .setParameter("id", user.getId())
                .getResultList();
        assertThat(b).isNotEmpty().hasSize(1);
        assertThat(b.get(0).getEmail()).isEqualTo(user.getEmail()).isEqualTo(m.getEmail());
        assertThat(b.get(0).getName()).isEqualTo(user.getName()).isEqualTo(m.getName());
    }

    @Test
    void findAllByIdNotNull() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();

        String query = "select user from User as user ";
        List<User> b = em.getEntityManager().createQuery(
                        query, User.class)
                .getResultList();
        assertThat(b).isEmpty();

        user = userRepository.save(user);
        List<User> m = userRepository.findAll();
        b = em.getEntityManager().createQuery(
                        query, User.class)
                .getResultList();
        assertThat(b).isNotEmpty().hasSize(1);
        assertThat(b.get(0).getEmail()).isEqualTo(user.getEmail()).isEqualTo(m.get(0).getEmail());
        assertThat(b.get(0).getName()).isEqualTo(user.getName()).isEqualTo(m.get(0).getName());
    }

    @Test
    void isEarlierItemBookedByUserTest() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).build();
        Item item2 = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription2).name(itemName2).available(itemAvailable2).build();
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(bookingStatus).build();
        Booking booking2 = Booking.builder().start(bookingStart).end(bookingEnd).status(bookingStatus).build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
            item = item.toBuilder().owner(user).build();
        item = itemRepository.save(item);
        user.addItem(item);
        em.persist(user);
            item2 = item2.toBuilder().owner(user2).build();
        item2 = itemRepository.save(item2);
        user2.addItem(item2);
        em.persist(user2);
            booking = booking.toBuilder().booker(user2).booked(item)
                    .start(LocalDateTime.now().plusSeconds(1))
                    .end(LocalDateTime.now().plusSeconds(2)).build();
            booking2 = booking.toBuilder().booker(user2).booked(item2)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).build();
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        booking = bookingRepository.save(booking);
        user2.addBooking(booking);
        em.persist(user2);
        item.addBooking(booking);
        em.persist(item);
        booking2 = bookingRepository.save(booking2);
        user2.addBooking(booking2);
        em.persist(user2);
        item2.addBooking(booking2);
        em.persist(item2);

        boolean b = userRepository.isEarlierItemBookedByUser(item.getId(), user2.getId());
        assertThat(b).isTrue();
        b = userRepository.isEarlierItemBookedByUser(item2.getId(), user2.getId());
        assertThat(b).isFalse();
        b = userRepository.isEarlierItemBookedByUser(item2.getId(), user.getId());
        assertThat(b).isFalse();
        b = userRepository.isEarlierItemBookedByUser(item.getId(), user.getId());
        assertThat(b).isFalse();
        b = userRepository.isEarlierItemBookedByUser(item2.getId(), user.getId());
        assertThat(b).isFalse();
    }

    @Test
    void isUserBookerTest() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).build();
        Item item2 = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription2).name(itemName2).available(itemAvailable2).build();
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(bookingStatus).build();
        Booking booking2 = Booking.builder().start(bookingStart).end(bookingEnd).status(bookingStatus).build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        item = item.toBuilder().owner(user).build();
        item = itemRepository.save(item);
            user.addItem(item);
            em.persist(user);
        item2 = item2.toBuilder().owner(user2).build();
        item2 = itemRepository.save(item2);
            user2.addItem(item2);
            em.persist(user2);
        booking = booking.toBuilder().booker(user2).booked(item)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2)).build();
        booking = bookingRepository.save(booking);
            user2.addBooking(booking);
            em.persist(user2);
            item.addBooking(booking);
            em.persist(item);

        boolean b = userRepository.isUserBooker(item.getId(), user2.getId());
        assertThat(b).isTrue();
        b = userRepository.isUserBooker(item2.getId(), user2.getId());
        assertThat(b).isFalse();
        b = userRepository.isUserBooker(item.getId(), user.getId());
        assertThat(b).isFalse();
        b = userRepository.isUserBooker(item2.getId(), user.getId());
        assertThat(b).isFalse();
    }

    @Test
    void isUserOwnerTest() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).build();
        Item item2 = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription2).name(itemName2).available(itemAvailable2).build();
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(bookingStatus).build();
        Booking booking2 = Booking.builder().start(bookingStart).end(bookingEnd).status(bookingStatus).build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        item = item.toBuilder().owner(null).build();
        user.addItem(item);
        item = itemRepository.save(item);
        item2 = item2.toBuilder().owner(null).build();
        user2.addItem(item2);
        item2 = itemRepository.save(item2);

        boolean b = userRepository.isUserOwner(item.getId(), user.getId());
        assertThat(b).isTrue();
        b = userRepository.isUserOwner(item.getId(), user2.getId());
        assertThat(b).isFalse();
        b = userRepository.isUserOwner(item2.getId(), user.getId());
        assertThat(b).isFalse();
        b = userRepository.isUserOwner(item2.getId(), user2.getId());
        assertThat(b).isTrue();
    }

    @Test
    void isUserOwnerOrBookerTest() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).build();
        Item item2 = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription2).name(itemName2).available(itemAvailable2).build();
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(bookingStatus).build();
        Booking booking2 = Booking.builder().start(bookingStart).end(bookingEnd).status(bookingStatus).build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        item = item.toBuilder().owner(user).build();
        item = itemRepository.save(item);
        item2 = item2.toBuilder().owner(user2).build();
        item2 = itemRepository.save(item2);
        booking = booking.toBuilder().booker(user2).booked(item)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2)).build();
        booking = bookingRepository.save(booking);

        boolean b = userRepository.isUserOwnerOrBooker(item.getId(), user.getId());
        assertThat(b).isTrue();
        b = userRepository.isUserOwnerOrBooker(item.getId(), user2.getId());
        assertThat(b).isTrue();
        b = userRepository.isUserOwnerOrBooker(item2.getId(), user.getId());
        assertThat(b).isFalse();
        b = userRepository.isUserOwnerOrBooker(item2.getId(), user2.getId());
        assertThat(b).isTrue();
    }

    @Test
    void isUserAbleToBookTest() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).build();
        Item item2 = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription2).name(itemName2).available(itemAvailable2).build();
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(bookingStatus).build();
        Booking booking2 = Booking.builder().start(bookingStart).end(bookingEnd).status(bookingStatus).build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        item = item.toBuilder().owner(user).build();
        item = itemRepository.save(item);
        item2 = item2.toBuilder().owner(user2).available(false).build();
        item2 = itemRepository.save(item2);
        final Long it1 = item.getId();
        final Long it2 = item2.getId();
        final Long us1 = user.getId();
        final Long us2 = user2.getId();

        HttpCustomException e = assertThrows(HttpCustomException.class, () -> userRepository.isUserAbleToBook(it1, us1));
        assertThat(e.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        e = assertThrows(HttpCustomException.class, () -> userRepository.isUserAbleToBook(it2, us1));
        assertThat(e.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        e = assertThrows(HttpCustomException.class, () -> userRepository.isUserAbleToBook(-1L, us1));
        assertThat(e.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        item2.setAvailable(true);
        boolean b = userRepository.isUserAbleToBook(item2.getId(), user.getId());
        assertThat(b).isTrue();
    }


    String userEmail = "qwerty@yandex.ru";
    String userName = "John";
    String itemName = "Щетка";
    String itemDescription = "something";
    Boolean itemAvailable = true;
    String userEmail2 = "qwerty@google.ru";
    String userName2 = "Sam";
    String itemName2 = "Куртка";
    String itemDescription2 = "nothing";
    Boolean itemAvailable2 = true;
    BookingStatus bookingStatus = BookingStatus.WAITING;
    LocalDateTime bookingStart = LocalDateTime.now().plusDays(2);
    LocalDateTime bookingEnd = LocalDateTime.now().plusDays(4);
    BookingStatus bookingStatus2 = BookingStatus.APPROVED;
    LocalDateTime bookingStart2 = LocalDateTime.now().plusDays(20);
    LocalDateTime bookingEnd2 = LocalDateTime.now().plusDays(40);
    Long userId = 2L;
    Long userId2 = 11L;
    Long itemId = 25L;
    Long itemId2 = 7L;
    Long bookingId = 13L;
    Long bookingId2 = 14L;

    Long requestId = 88L;
    String requestDescription = "req desc";
    LocalDateTime requestCreated = LocalDateTime.now();
    Long requestId2 = 886L;
    String requestDescription2 = "req2 desc";
    LocalDateTime requestCreated2 = LocalDateTime.now().minusDays(8);
    String commentText = "comment text";
    Long commentId = 77L;
    LocalDateTime commentCreated = LocalDateTime.now().minusDays(22);
    String commentText2 = "comment text2";
    Long commentId2 = 771L;
    LocalDateTime commentCreated2 = LocalDateTime.now().minusDays(220);

}