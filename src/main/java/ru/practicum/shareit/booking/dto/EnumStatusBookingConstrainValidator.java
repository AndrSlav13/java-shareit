/*
https://www.baeldung.com/javax-validations-enums
**/
package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class EnumStatusBookingConstrainValidator implements ConstraintValidator<EnumStatusBookingConstrain, String> {
    private Pattern pattern;

    @Override
    public void initialize(EnumStatusBookingConstrain annotation) {
        try {
            pattern = Pattern.compile(annotation.regexp());
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Given regex is invalid", e);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        Matcher m = pattern.matcher(value);
        return m.matches() && Arrays.stream(BookingStatus.values()).anyMatch(val -> val.name() == value);
    }
}