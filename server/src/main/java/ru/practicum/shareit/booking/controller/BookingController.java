package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.BookingStateForOutput;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDTO.Controller.ReturnBookItemDTO addBooking(@RequestBody BookingDTO.Controller.NewBookItemDTO bookItemDTO,
                                                              @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.addItem(BookingDTO.Controller.Mapper.toBooking(bookItemDTO), bookerId, bookItemDTO.getItemId());
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDTO.Controller.ReturnBookItemDTO setApproved(@PathVariable Long bookingId,
                                                               @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                               @RequestParam String approved) {
        return bookingService.setApprove(bookingId, bookerId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDTO.Controller.ReturnBookItemDTO getBooking(@PathVariable Long bookingId,
                                                              @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.findByIdAndBookerOrIdAndOwner(bookingId, bookerId);
    }

    @GetMapping
    public List<BookingDTO.Controller.ReturnBookItemDTO> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                                           @RequestParam(defaultValue = "ALL") String state,
                                                                           @RequestParam(defaultValue = "0") Integer from,
                                                                           @RequestParam(defaultValue = "10") Integer size) {
        BookingStateForOutput st = BookingStateForOutput.vOf(state);
        return bookingService.findByBooker(bookerId, st, from, size);
    }

    @GetMapping(path = "/owner")
    public List<BookingDTO.Controller.ReturnBookItemDTO> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                                            @RequestParam(defaultValue = "ALL") String state,
                                                                            @RequestParam(defaultValue = "0") Integer from,
                                                                            @RequestParam(defaultValue = "10") Integer size) {
        BookingStateForOutput st = BookingStateForOutput.vOf(state);
        return bookingService.findByOwner(bookerId, st, from, size);
    }
}
