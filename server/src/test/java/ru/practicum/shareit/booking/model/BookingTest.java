package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class BookingTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void bookingTests() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).owner(user).build();
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(BookingStatus.WAITING).booked(item).booker(user2).build();
        ItemRequest request = ItemRequest.builder().items(new ArrayList<>()).requestor(user).description("lala").build();
        Comment comment = Comment.builder().itemCommented(item).text("qualcosa").build();

        user = userRepository.save(user);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);
        request = requestRepository.save(request);
        comment = commentRepository.save(comment);

        user.addItem(item);
        user2.addBooking(booking);
        user.addRequest(request);
        item.addComment(comment);

        assertThat(user.getItems()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(user.getItems().get(0).getDescription()).isEqualTo(item.getDescription());
        assertThat(user.getRequests()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(user.getRequests().get(0).getDescription()).isEqualTo(request.getDescription());
        assertThat(user2.getBookings()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(user2.getBookings().get(0).getBooked().getName()).isEqualTo(item.getName());
        assertThat(comment.getItemCommented().getName()).isEqualTo(item.getName());
        assertThat(item.getComments().get(0).getText()).isEqualTo(comment.getText());
    }

    @Test
    void bookingHashEqualsTests() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).owner(user).build();
        LocalDateTime bookingStart = LocalDateTime.now().plusHours(1);
        LocalDateTime bookingEnd = LocalDateTime.now().plusHours(10);
        final Booking booking = Booking.builder()
                .start(bookingStart)
                .end(bookingEnd).status(BookingStatus.WAITING).booked(item).booker(user2).build();
        final Booking booking2 = Booking.builder()
                .start(bookingStart.plusHours(4))
                .end(bookingEnd.minusHours(20)).status(BookingStatus.WAITING).booked(item).booker(user2).build();
        final Booking booking3 = Booking.builder()
                .start(bookingStart.plusHours(100))
                .end(bookingEnd.plusHours(200)).status(BookingStatus.WAITING).booked(item).booker(user2).build();

        bookingRepository.save(booking);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);

        assertThat(booking.hashCode()).isEqualTo(booking.getId().hashCode());
        HttpCustomException e = assertThrows(HttpCustomException.class, () -> booking.equals(booking2));
        assertThat(e.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(booking.equals(booking)).isTrue();
        assertThat(booking.equals(booking3)).isFalse();
    }

    String userEmail = "qwerty@yandex.ru";
    String userName = "John";
    String itemName = "Щетка";
    String itemDescription = "something";
    Boolean itemAvailable = true;
    String userEmail2 = "qwerty@google.ru";
    String userName2 = "Sam";
    LocalDateTime bookingStart = LocalDateTime.now().plusDays(2);
    LocalDateTime bookingEnd = LocalDateTime.now().plusDays(4);
}