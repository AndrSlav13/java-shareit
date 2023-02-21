package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateForOutput;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingStore;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingStore,
                              UserService userService,
                              ItemService itemService) {
        this.bookingStore = bookingStore;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDTO.Controller.ReturnBookItemDTO addItem(Booking booking, Long bookerId, Long itemId) {
        User user = userService.getSimpleUser(bookerId);
        Item it = itemService.getSimpleItem(itemId);
        BookingService.validate(booking);
        UserService.validate(user);
        ItemService.validate(it);

        userService.isUserAbleToBook(itemId, bookerId);

        booking.setBooked(it);
        user.addBooking(booking);
        return BookingDTO.Controller.Mapper.toReturnBookItemDTO(bookingStore.save(booking));
    }

    @Override
    public BookingDTO.Controller.ReturnBookItemDTO setApprove(Long bookingId, Long bookerId, String approved) {
        User booker = userService.getSimpleUser(bookerId);
        Booking booking = bookingStore.findById(bookingId).get();
        UserService.validate(booker);
        BookingService.validate(booking);

        if (!userService.isUserOwner(booking.getBooked().getId(), bookerId))
            throw new HttpCustomException(HttpStatus.NOT_FOUND, "Status can be changed solely by owner");

        if (booking.getStatus().equals(BookingStatus.APPROVED))
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "Booking is already approved");
        if (approved.equals("true")) booking.setStatus(BookingStatus.APPROVED);
        if (approved.equals("false")) booking.setStatus(BookingStatus.REJECTED);
        return BookingDTO.Controller.Mapper.toReturnBookItemDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTO.Controller.ReturnBookItemDTO findByIdAndBookerOrIdAndOwner(Long bookingId, Long bookerId) {
        User booker = userService.getSimpleUser(bookerId);
        Booking booking = bookingStore.findById(bookingId).orElse(null);
        UserService.validate(booker);
        BookingService.validate(booking);

        if (!userService.isUserOwnerOrBooker(booking.getBooked().getId(), bookerId))
            throw new HttpCustomException(HttpStatus.NOT_FOUND, "The user didn't book and doesn't owns this item");

        return BookingDTO.Controller.Mapper.toReturnBookItemDTO(bookingStore.findByIdAndBookerOrIdAndOwner(bookingId, bookerId).get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO.Controller.ReturnBookItemDTO> findByBooker(Long bookerId, String state) {
        User booker = userService.getSimpleUser(bookerId);
        UserService.validate(booker);

        return bookingStore.findByBooker(bookerId, BookingStateForOutput.vOf(state)).stream()
                .map(booking -> BookingDTO.Controller.Mapper.toReturnBookItemDTO(booking))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO.Controller.ReturnBookItemDTO> findByOwner(Long bookerId, String state) {
        User booker = userService.getSimpleUser(bookerId);
        UserService.validate(booker);

        return bookingStore.findByOwner(bookerId, BookingStateForOutput.vOf(state)).stream()
                .map(booking -> BookingDTO.Controller.Mapper.toReturnBookItemDTO(booking))
                .collect(Collectors.toList());
    }
}
