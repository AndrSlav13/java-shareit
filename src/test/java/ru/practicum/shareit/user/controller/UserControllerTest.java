package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDTO.Controller.NewUserDTO userDTO;

    @Test
    void addUserTest() throws Exception {
        userDTO = UserDTO.Controller.NewUserDTO.newUserDTOBuilder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
        Mockito
                .when(userService.addUser(Mockito.any()))
                .thenAnswer(i -> {
                    User b = (User) (i.getArguments()[0]);
                    return UserDTO.Controller.Mapper.toReturnUserDTO(b);
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void addUserTestException() throws Exception {
        userDTO = UserDTO.Controller.NewUserDTO.newUserDTOBuilder()
                .name("")
                .email(user.getEmail())
                .build();
        Mockito
                .when(userService.addUser(Mockito.any()))
                .thenAnswer(i -> {
                    User b = (User) (i.getArguments()[0]);
                    return UserDTO.Controller.Mapper.toReturnUserDTO(b);
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserByIdTest() throws Exception {
        userDTO = UserDTO.Controller.NewUserDTO.newUserDTOBuilder()
                .name(user2.getName())
                .email(user2.getEmail())
                .build();
        Mockito
                .when(userService.updateUserById(Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    User b = (User) (i.getArguments()[0]);
                    user = user.toBuilder().name(b.getName()).email(b.getEmail()).build();
                    return UserDTO.Controller.Mapper.toReturnUserDTO(user);
                });

        mvc.perform(patch("/users/{id}", user.getId())
                        .content(mapper.writeValueAsString(userDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(user2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user2.getEmail()));
    }


    String userEmail = "qwerty@yandex.ru";
    String userName = "John";
    String userEmail2 = "qwerty@google.ru";
    String userName2 = "Sam";
    Long userId = 2L;
    Long userId2 = 11L;
    User user = null;
    User user2 = null;

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
        user2 = User.builder()
                .id(userId2)
                .items(new ArrayList<>())
                .bookings(new ArrayList<>())
                .requests(new ArrayList<>())
                .email(userEmail2)
                .name(userName2)
                .build();

    }

}
