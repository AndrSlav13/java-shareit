package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDTO.Controller.ReturnBookItemDTO addBooking(@RequestBody BookingDTO.Controller.NewBookItemDTO bookItemDTO,
                                                              @RequestHeader("X-Sharer-User-Id") Optional<Long> bookerId) {
        return bookingService.addItem(BookingDTO.Controller.Mapper.toBooking(bookItemDTO), bookerId.get(), bookItemDTO.getItemId());
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDTO.Controller.ReturnBookItemDTO setApproved(@PathVariable Long bookingId,
                                                               @RequestHeader("X-Sharer-User-Id") Optional<Long> bookerId,
                                                               @RequestParam @Valid @NotEmpty String approved) {
        return bookingService.setApprove(bookingId,
                bookerId.get(),
                approved
        );
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDTO.Controller.ReturnBookItemDTO getBooking(@PathVariable Long bookingId,
                                                              @RequestHeader("X-Sharer-User-Id") Optional<Long> bookerId) {
        return bookingService.findByIdAndBookerOrIdAndOwner(bookingId, bookerId.get());
    }

    @GetMapping
    public List<BookingDTO.Controller.ReturnBookItemDTO> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Optional<Long> bookerId,
                                                                           @RequestParam(defaultValue = "ALL") @Valid @NotEmpty String state) {
        return bookingService.findByBooker(bookerId.get(), state);
    }

    @GetMapping(path = "/owner")
    public List<BookingDTO.Controller.ReturnBookItemDTO> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") Optional<Long> bookerId,
                                                                            @RequestParam(defaultValue = "ALL") @Valid @NotEmpty String state) {
        return bookingService.findByOwner(bookerId.get(), state);
    }
}
