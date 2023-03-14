package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ExceptionsTest {

    @Test
    void myTests() {
        try {
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, "message");
        } catch (HttpCustomException e) {
            assertThat(e.getMessage()).isEqualTo("400 : message");
            assertThat(e.getMessageWithoutCode()).isEqualTo("message");
            e.setMessage("qwe");
            assertThat(e.getMessageWithoutCode()).isEqualTo("qwe");
            assertThat(e.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
        try {
            throw new HttpCustomException("message");
        } catch (HttpCustomException e) {
            assertThat(e.getMessage()).isEqualTo("406 : message");
            assertThat(e.getCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        }
        try {
            throw new HttpCustomException(HttpStatus.NOT_FOUND);
        } catch (HttpCustomException e) {
            assertThat(e.getMessage()).isEqualTo("404 : Unknown net error");
            assertThat(e.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
        try {
            throw new HttpCustomException();
        } catch (HttpCustomException e) {
            assertThat(e.getMessage()).isEqualTo("400 : Unknown net error");
            assertThat(e.getCode()).isEqualTo(400);
        }
    }

}
