package ru.practicum.shareit.user.service;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDTO.Controller.ReturnUserDTO getUserById(Long id);

    UserDTO.Controller.ReturnUserDTO addUser(User user);

    UserDTO.Controller.ReturnUserDTO updateUserById(User user, Long idUser);

    List<UserDTO.Controller.ReturnUserDTO> getAllUsers();

    List<UserDTO.Controller.ReturnUserDTO> deleteUserById(Long id);

    User getSimpleUser(Long id);

    boolean isUserBooker(Long itemId, Long userId);

    boolean isEarlierItemBookedByUser(Long itemId, Long userId);

    boolean isUserOwner(Long itemId, Long userId);

    boolean isUserOwnerOrBooker(Long itemId, Long userId);

    boolean isUserAbleToBook(Long itemId, Long userId);

    static boolean validate(User user) {
        if (user == null) throw new HttpCustomException(HttpStatus.NOT_FOUND, "The user is absent");
        return true;
    }
}
