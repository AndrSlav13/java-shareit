package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserJSONTest {
    @Autowired
    private JacksonTester<UserDTO.Controller.ReturnUserDTO> json;

    @Test
    void testUserDto() throws Exception {
        UserDTO.Controller.ReturnUserDTO userDto = UserDTO.Controller.ReturnUserDTO.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();

        JsonContent<UserDTO.Controller.ReturnUserDTO> result = json.write(userDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(user.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(user.getEmail());
    }

    String userEmail = "qwerty@yandex.ru";
    String userName = "John";
    Long userId = 2L;
    User user = null;


    @BeforeEach
    public void before() {
        user = User.builder()
                .id(userId)
                .items(new ArrayList<>())
                .bookings(new ArrayList<>())
                .requests(new ArrayList<>())
                .email(userEmail)
                .name(userName)
                .build();
    }

}
