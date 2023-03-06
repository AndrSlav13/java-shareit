package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping(path = "/{id}")
    @ResponseBody
    public UserDTO.Controller.ReturnUserDTO getUserById(@NotNull @Positive @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PatchMapping(path = "/{id}")
    @ResponseBody
    public UserDTO.Controller.ReturnUserDTO updateUserById(@NotNull @Positive @PathVariable(value = "id") Long idUser,
                                                           @RequestBody @Valid UserDTO.Controller.UpdateUserDTO userDTO) {
        return userService.updateUserById(UserDTO.Controller.Mapper.toUser(userDTO), idUser);
    }

    @GetMapping
    @ResponseBody
    public List<UserDTO.Controller.ReturnUserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseBody
    public UserDTO.Controller.ReturnUserDTO addUser(@RequestBody @Valid UserDTO.Controller.NewUserDTO user) {
        return userService.addUser(UserDTO.Controller.Mapper.toUser(user));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseBody
    public List<UserDTO.Controller.ReturnUserDTO> deleteUserById(@NotNull @Positive @PathVariable Long id) {
        return userService.deleteUserById(id);
    }

}
