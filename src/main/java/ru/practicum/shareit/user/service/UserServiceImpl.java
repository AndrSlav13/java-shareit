package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userStore;

    @Autowired
    public UserServiceImpl(UserRepository userStore) {
        this.userStore = userStore;
    }

    @Override
    public User getSimpleUser(Long id) {
        User user = userStore.findById(id).orElseThrow(() ->
                new HttpCustomException(HttpStatus.NOT_FOUND, "User with id=" + id + " is absent")
        );
        UserService.validate(user);
        return user;
    }

    @Override
    public UserDTO.Controller.ReturnUserDTO getUserById(Long id) {
        User user = getSimpleUser(id);
        return UserDTO.Controller.Mapper.toReturnUserDTO(user);
    }

    @Override
    public List<UserDTO.Controller.ReturnUserDTO> getAllUsers() {
        return userStore.findAllByIdNotNull().stream()
                .map(user -> UserDTO.Controller.Mapper.toReturnUserDTO(user))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDTO.Controller.ReturnUserDTO addUser(User user) {
        User usr = userStore.save(user);
        return UserDTO.Controller.Mapper.toReturnUserDTO(usr);
    }

    @Transactional
    @Override
    public UserDTO.Controller.ReturnUserDTO updateUserById(User user, Long idUser) {
        User usr = getSimpleUser(idUser);
        if (user.getName() != null) usr.setName(user.getName());
        if (user.getEmail() != null) usr.setEmail(user.getEmail());
        return UserDTO.Controller.Mapper.toReturnUserDTO(usr);
    }

    @Transactional
    @Override
    public List<UserDTO.Controller.ReturnUserDTO> deleteUserById(Long id) {
        userStore.deleteById(id);
        return getAllUsers();
    }

    @Override
    public boolean isUserBooker(Long itemId, Long userId) {
        return userStore.isUserBooker(itemId, userId);
    }

    @Override
    public boolean isEarlierItemBookedByUser(Long itemId, Long userId) {
        return userStore.isEarlierItemBookedByUser(itemId, userId);
    }

    @Override
    public boolean isUserOwner(Long itemId, Long userId) {
        return userStore.isUserOwner(itemId, userId);
    }

    @Override
    public boolean isUserOwnerOrBooker(Long itemId, Long userId) {
        return userStore.isUserOwnerOrBooker(itemId, userId);
    }

    @Override
    public boolean isUserAbleToBook(Long itemId, Long userId) {
        return userStore.isUserAbleToBook(itemId, userId);
    }
}
