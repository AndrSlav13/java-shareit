package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping(path = "/{id}")
    @ResponseBody
    public UserDTO.Controller.ReturnUserDTO getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PatchMapping(path = "/{id}")
    @ResponseBody
    public UserDTO.Controller.ReturnUserDTO getUserById(@PathVariable(value = "id") Integer idUser,
                                                        @RequestBody @Valid UserDTO.Controller.NewUserDTO userDTO) {
        return userService.updateUserById(UserDTO.Controller.Mapper.toUser(userDTO, idUser));
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
    public List<UserDTO.Controller.ReturnUserDTO> deleteUserById(@PathVariable Integer id) {
        return userService.deleteUserById(id);
    }

}
