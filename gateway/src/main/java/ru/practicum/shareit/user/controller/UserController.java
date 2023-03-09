package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Object> getUserById(@NotNull @Positive @PathVariable Long id) {
        return userClient.getUser(0L, id);
    }

    @PatchMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Object> updateUserById(@NotNull @Positive @PathVariable(value = "id") Long idUser,
                                                 @RequestBody @Valid UserDTO.Controller.UpdateUserDTO userDTO) {
        return userClient.updateUser(userDTO, idUser);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> getAllUsers(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        return userClient.getAllUsers(0L, from, size);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDTO.Controller.NewUserDTO user) {
        return userClient.addUser(user);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Object> deleteUserById(@NotNull @Positive @PathVariable Long id) {
        return userClient.deleteUser(0L, id);
    }

}
