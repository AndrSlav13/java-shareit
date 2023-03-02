/*
https://www.baeldung.com/javax-validations-enums
**/
package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumStatusBookingConstrainValidator implements ConstraintValidator<EnumStatusBookingConstrain, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return Arrays.stream(BookingStatus.values()).anyMatch(val -> val.name().equals(value));
    }
}