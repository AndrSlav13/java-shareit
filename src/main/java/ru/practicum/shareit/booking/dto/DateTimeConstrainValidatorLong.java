package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;

public class DateTimeConstrainValidatorLong implements ConstraintValidator<DateTimeConstrainLong, Long> {

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        Instant ins = Instant.ofEpochMilli(value);

        return ins.isAfter(Instant.now());
    }
}