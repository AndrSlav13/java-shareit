package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;

public class DateTimeConstrainValidator implements ConstraintValidator<DateTimeConstrain, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        Instant ins = BookingDTO.stringToInstant(value);

        return ins.isAfter(Instant.now());
    }
}