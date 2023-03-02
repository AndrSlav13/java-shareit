package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RequestTest {

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

    @Autowired
    private TestEntityManager em;

    @Test
    void requestTests() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        Item item = Item.builder().requests(new ArrayList<>()).bookings(new ArrayList<>()).comments(new ArrayList<>())
                .description(itemDescription).name(itemName).available(itemAvailable).owner(user).build();
        Booking booking = Booking.builder().start(bookingStart).end(bookingEnd).status(BookingStatus.WAITING).booked(item).booker(user2).build();
        ItemRequest request = ItemRequest.builder().items(new ArrayList<>()).requestor(user).description("lala").build();

        user = userRepository.save(user);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);
        request = requestRepository.save(request);

        request.addRecommendedItems(item);
        assertThat(request.getItems()).isNotNull().isNotEmpty().hasSize(1);
        request.removeRecommendedItems(item);
        assertThat(request.getItems()).isEmpty();
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
