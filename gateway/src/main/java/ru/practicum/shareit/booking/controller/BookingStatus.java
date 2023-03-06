package ru.practicum.shareit.booking.controller;

public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELED;

    public static boolean contains(String s) {
        for (BookingStatus b : BookingStatus.values())
            if (s.equals(b.name())) return true;
        return false;
    }
}
