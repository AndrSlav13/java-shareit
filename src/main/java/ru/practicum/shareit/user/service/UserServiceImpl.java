package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.storage.ItemStore;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStore;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserStore userStore;

    public UserServiceImpl(UserStore userStore, ItemStore itemStore) {
        this.userStore = userStore;
    }

    @Override
    public UserDTO.Controller.ReturnUserDTO getUserById(Integer id) {
        return UserDTO.Controller.Mapper.toReturnUserDTO(userStore.getUserById(id));
    }

    @Override
    public UserDTO.Controller.ReturnUserDTO addUser(User user) {
        return UserDTO.Controller.Mapper.toReturnUserDTO(userStore.addUser(UserDTO.Database.Mapper.toDBUserDTO(user)));
    }

    @Override
    public UserDTO.Controller.ReturnUserDTO updateUserById(User user) {
        return UserDTO.Controller.Mapper.toReturnUserDTO(userStore.updateUser(UserDTO.Database.Mapper.toDBUserDTO(user)));
    }

    @Override
    public List<UserDTO.Controller.ReturnUserDTO> getAllUsers() {
        return userStore.getAllUsers().stream()
                .map(user -> UserDTO.Controller.Mapper.toReturnUserDTO(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO.Controller.ReturnUserDTO> deleteUserById(Integer id) {
        return userStore.deleteUserById(id).stream()
                .map(user -> UserDTO.Controller.Mapper.toReturnUserDTO(user))
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsEmail(String email) {
        return userStore.containsEmail(email);
    }

    @Override
    public boolean containsUserId(Integer id) {
        return userStore.containsUserId(id);
    }
}
