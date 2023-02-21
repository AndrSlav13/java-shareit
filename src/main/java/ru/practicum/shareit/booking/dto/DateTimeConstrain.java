package ru.practicum.shareit.booking.dto;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.format.DateTimeFormatter;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = DateTimeConstrainValidator.class)
public @interface DateTimeConstrain {
    String format = DateTimeFormatter.ISO_DATE_TIME.toString();

    String message() default "Allowed formats: ";
}