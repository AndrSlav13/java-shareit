package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateForOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
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

    User user = null;
    User user2 = null;
    Item item = null;
    Item item2 = null;
    Booking booking = null;
    Booking booking2 = null;

    ItemRequest request = null;
    ItemRequest request2 = null;
    Comment comment = null;
    Comment comment2 = null;

}