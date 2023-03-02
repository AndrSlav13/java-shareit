package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateForOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findAllBookings() {
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(bookingStatus).build();

        String query = "select booking from Booking as booking ";
        List<Booking> b = em.getEntityManager().createQuery(
                        query, Booking.class)
                .getResultList();
        assertThat(b).isEmpty();

        bookingRepository.save(booking);
        List<Booking> m = bookingRepository.findAll();
        b = em.getEntityManager().createQuery(
                        query, Booking.class)
                .getResultList();
        assertThat(b).isNotEmpty().hasSize(1);
        assertThat(b.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(b.get(0).getEnd()).isEqualTo(booking.getEnd());
    }


    @Test
    void findByBookerTest() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).build();
        Item item2 = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription2).name(itemName2).available(itemAvailable2).build();
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(BookingStatus.WAITING).build();
        Booking booking2 = Booking.builder().start(bookingStart).end(bookingEnd).status(BookingStatus.WAITING).build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        item = item.toBuilder().owner(user2).build();
        item = itemRepository.save(item);
        booking = booking.toBuilder().booker(user).booked(item)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2)).build();
        booking = bookingRepository.save(booking);

        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        List<Booking> b = bookingRepository.findByBooker(user.getId(), BookingStateForOutput.ALL, 0, 10);
        assertThat(b).isNotEmpty().hasSize(1);
        b = bookingRepository.findByBooker(user.getId(), BookingStateForOutput.FUTURE, 0, 10);
        assertThat(b).isEmpty();
        b = bookingRepository.findByBooker(user.getId(), BookingStateForOutput.REJECTED, 0, 10);
        assertThat(b).isEmpty();
        b = bookingRepository.findByBooker(user.getId(), BookingStateForOutput.PAST, 0, 10);
        assertThat(b).isNotEmpty().hasSize(1);
        b = bookingRepository.findByBooker(user.getId(), BookingStateForOutput.CURRENT, 0, 10);
        assertThat(b).isEmpty();
        booking.setStatus(BookingStatus.REJECTED);
        b = bookingRepository.findByBooker(user.getId(), BookingStateForOutput.REJECTED, 0, 10);
        assertThat(b).isNotEmpty().hasSize(1);
        b = bookingRepository.findByBooker(user2.getId(), BookingStateForOutput.ALL, 0, 10);
        assertThat(b).isEmpty();
    }

    @Test
    void findByOwnerTest() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).build();
        Item item2 = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription2).name(itemName2).available(itemAvailable2).build();
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(BookingStatus.WAITING).build();
        Booking booking2 = Booking.builder().start(bookingStart).end(bookingEnd).status(BookingStatus.WAITING).build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        item = item.toBuilder().owner(user2).build();
        item = itemRepository.save(item);
        booking = booking.toBuilder().booker(user).booked(item)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2)).build();
        booking = bookingRepository.save(booking);

        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        List<Booking> b = bookingRepository.findByOwner(user2.getId(), BookingStateForOutput.ALL, 0, 10);
        assertThat(b).isNotEmpty().hasSize(1);
        b = bookingRepository.findByOwner(user2.getId(), BookingStateForOutput.FUTURE, 0, 10);
        assertThat(b).isEmpty();
        b = bookingRepository.findByOwner(user2.getId(), BookingStateForOutput.REJECTED, 0, 10);
        assertThat(b).isEmpty();
        b = bookingRepository.findByOwner(user2.getId(), BookingStateForOutput.PAST, 0, 10);
        assertThat(b).isNotEmpty().hasSize(1);
        b = bookingRepository.findByOwner(user2.getId(), BookingStateForOutput.CURRENT, 0, 10);
        assertThat(b).isEmpty();
        booking.setStatus(BookingStatus.REJECTED);
        b = bookingRepository.findByOwner(user2.getId(), BookingStateForOutput.REJECTED, 0, 10);
        assertThat(b).isNotEmpty().hasSize(1);
        b = bookingRepository.findByOwner(user.getId(), BookingStateForOutput.ALL, 0, 10);
        assertThat(b).isEmpty();
    }

    @Test
    void findByIdAndBookerOrIdAndOwnerTest() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).build();
        Item item2 = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription2).name(itemName2).available(itemAvailable2).build();
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(BookingStatus.WAITING).build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        item = item.toBuilder().owner(user).build();
        item = itemRepository.save(item);
        item2 = item2.toBuilder().owner(user2).build();
        item2 = itemRepository.save(item2);
        booking = booking.toBuilder().booker(user).booked(item2)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2)).build();
        booking = bookingRepository.save(booking);

        List<Booking> b = bookingRepository.findByIdAndBookerOrIdAndOwner(booking.getId(), user.getId());
        assertThat(b).isNotEmpty().hasSize(1);
        b = bookingRepository.findByIdAndBookerOrIdAndOwner(booking.getId(), user2.getId());
        assertThat(b).isNotEmpty().hasSize(1);
        b = bookingRepository.findByIdAndBookerOrIdAndOwner(-1L, user.getId());
        assertThat(b).isEmpty();
        b = bookingRepository.findByIdAndBookerOrIdAndOwner(booking.getId(), -1L);
        assertThat(b).isEmpty();
    }

    @Test
    void lastNextBookingsTest() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).build();
        Item item2 = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription2).name(itemName2).available(itemAvailable2).build();
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(BookingStatus.WAITING).build();
        Booking booking2 = Booking.builder().start(bookingStart).end(bookingEnd).status(BookingStatus.WAITING).build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        item = item.toBuilder().owner(user).build();
        item = itemRepository.save(item);
        user.addItem(item);
        item2 = item2.toBuilder().owner(user2).build();
        item2 = itemRepository.save(item2);
        user2.addItem(item2);
        booking = booking.toBuilder().booker(user).booked(item2)
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2)).build();
        booking = bookingRepository.save(booking);
        user.addBooking(booking);
        item2.addBooking(booking);


        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        booking2 = booking2.toBuilder().booker(user2).booked(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).build();
        booking2 = bookingRepository.save(booking2);

        List<Booking> b = bookingRepository.findNextBookings(user.getId(), List.of(item.getId()), LocalDateTime.now());
        assertThat(b).isNotEmpty().hasSize(1);
        b = bookingRepository.findNextBookings(user.getId(), List.of(item2.getId()), LocalDateTime.now());
        assertThat(b).isEmpty();
        b = bookingRepository.findLastBookings(user2.getId(), List.of(item.getId()), LocalDateTime.now());
        assertThat(b).isEmpty();
        b = bookingRepository.findLastBookings(user2.getId(), List.of(item2.getId()), LocalDateTime.now());
        assertThat(b).isNotEmpty().hasSize(1);
        b = bookingRepository.findNextBookings(user2.getId(), List.of(item2.getId()), LocalDateTime.now());
        assertThat(b).isEmpty();
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

}