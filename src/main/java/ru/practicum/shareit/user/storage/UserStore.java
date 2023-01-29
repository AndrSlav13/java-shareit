package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStore {
    User getUserById(Integer id);

    User addUser(UserDTO.Database.DBUserDTO userDTO);

    User updateUser(UserDTO.Database.DBUserDTO userDTO);

    List<User> getAllUsers();

    List<User> deleteUserById(Integer id);

    boolean containsEmail(String email);

    boolean containsUserId(Integer id);
}