package ru.practicum.shareit.booking.dto;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;

public class DateTimeConstrainValidator
        implements ConstraintValidator<DateTimeConstrain, Object> {

    private String start;
    private String end;

    public void initialize(DateTimeConstrain constraintAnnotation) {
        this.start = constraintAnnotation.start();
        this.end = constraintAnnotation.end();
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {

        Object fieldValueStart = new BeanWrapperImpl(value)
                .getPropertyValue(start);
        Object fieldValueEnd = new BeanWrapperImpl(value)
                .getPropertyValue(end);

        Instant startI = null;
        Instant endI = null;
        if (fieldValueStart == null) return false;
        else {
            startI = BookingDTO.stringToInstant((String) fieldValueStart);
            if (startI.isBefore(Instant.now())) return false;
        }
        if (fieldValueEnd == null) return false;
        else {
            endI = BookingDTO.stringToInstant((String) fieldValueEnd);
            if (startI.isAfter(endI) || startI.equals(endI)) return false;
        }
        return true;
    }
}