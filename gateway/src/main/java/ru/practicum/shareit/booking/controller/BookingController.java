package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestBody @Valid BookingDTO.Controller.NewBookItemDTO bookItemDTO,
                                                              @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingClient.bookItem(bookerId, bookItemDTO);
    }

    @PatchMapping(path = "/{bookingId}")
    public ResponseEntity<Object> setApproved(@NotNull @Positive @PathVariable Long bookingId,
                                                               @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                               @Pattern(regexp = "(?i)^trUe$|^faLse$") @RequestParam String approved) {
        return bookingClient.setApprove(bookerId, bookingId, approved.toLowerCase());
    }

    @GetMapping(path = "/{bookingId}")
    public ResponseEntity<Object> getBooking(@NotNull @Positive @PathVariable Long bookingId,
                                                              @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingClient.getBooking(bookerId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfUser(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                                                          @RequestParam(defaultValue = "ALL") String state,
                                                                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingStateForOutput st = BookingStateForOutput.vOf(state);
        return bookingClient.getBookingsOfUser(bookerId, st, from, size);
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                                            @RequestParam(defaultValue = "ALL") String state,
                                                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                            @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingStateForOutput st = BookingStateForOutput.vOf(state);
        return bookingClient.getBookingsOfOwner(bookerId, st, from, size);
    }
}
