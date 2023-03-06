package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.BookingStateForOutput;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.HttpCustomException;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDTO.Controller.ReturnBookItemDTO addBooking(@RequestBody @Valid BookingDTO.Controller.NewBookItemDTO bookItemDTO,
                                                              @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.addItem(BookingDTO.Controller.Mapper.toBooking(bookItemDTO), bookerId, bookItemDTO.getItemId());
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDTO.Controller.ReturnBookItemDTO setApproved(@NotNull @Positive @PathVariable Long bookingId,
                                                               @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                               @Pattern(regexp = "(?i)^trUe$|^faLse$") @RequestParam String approved) {
        return bookingService.setApprove(bookingId, bookerId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDTO.Controller.ReturnBookItemDTO getBooking(@NotNull @Positive @PathVariable Long bookingId,
                                                              @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.findByIdAndBookerOrIdAndOwner(bookingId, bookerId);
    }

    @GetMapping
    public List<BookingDTO.Controller.ReturnBookItemDTO> getBookingsOfUser(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                                           @RequestParam(defaultValue = "ALL") String state,
                                                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingStateForOutput st = BookingStateForOutput.vOf(state);
        return bookingService.findByBooker(bookerId, st, from, size);
    }

    @GetMapping(path = "/owner")
    public List<BookingDTO.Controller.ReturnBookItemDTO> getBookingsOfOwner(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                                            @RequestParam(defaultValue = "ALL") String state,
                                                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                            @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingStateForOutput st = BookingStateForOutput.vOf(state);
        return bookingService.findByOwner(bookerId, st, from, size);
    }
}
