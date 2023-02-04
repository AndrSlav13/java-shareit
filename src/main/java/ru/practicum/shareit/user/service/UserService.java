package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDTO.Controller.ReturnUserDTO getUserById(Integer id);

    UserDTO.Controller.ReturnUserDTO addUser(User user);

    UserDTO.Controller.ReturnUserDTO updateUserById(User user);

    List<UserDTO.Controller.ReturnUserDTO> getAllUsers();

    List<UserDTO.Controller.ReturnUserDTO> deleteUserById(Integer id);

    boolean containsEmail(String email);

    boolean containsUserId(Integer id);
}
