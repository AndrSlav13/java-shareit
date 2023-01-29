package ru.practicum.shareit.user.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class UserStoreImpl implements UserStore {
    private static final AtomicInteger count = new AtomicInteger(1);  //Счетчик id
    private HashMap<Integer, UserDTO.Database.DBUserDTO> userStore;

    public UserStoreImpl() {
        userStore = new HashMap<>();
    }

    @Override
    public User getUserById(Integer id) {
        if (!userStore.containsKey(id)) throw new HttpCustomException(HttpStatus.NOT_FOUND, this.getClass().toString());

        return UserDTO.Database.Mapper.ToUser(userStore.get(id));
    }

    @Override
    public User addUser(UserDTO.Database.DBUserDTO userDTO) {
        validate(userDTO);
        Integer cnt = count.getAndIncrement();
        userDTO.setId(cnt);
        userStore.put(cnt, userDTO);
        return UserDTO.Database.Mapper.ToUser(userDTO);
    }

    @Override
    public List<User> getAllUsers() {
        return userStore.values().stream()
                .map(userDTO -> UserDTO.Database.Mapper.ToUser(userDTO))
                .collect(Collectors.toList());
    }

    @Override
    public User updateUser(UserDTO.Database.DBUserDTO userDTO) {
        validateUpdate(userDTO);

        UserDTO.Database.DBUserDTO usr = userStore.get(userDTO.getId());
        if (userDTO.getEmail() != null) usr.setEmail(userDTO.getEmail());
        if (userDTO.getName() != null) usr.setName(userDTO.getName());

        return UserDTO.Database.Mapper.ToUser(userStore.get(userDTO.getId()));
    }

    @Override
    public List<User> deleteUserById(Integer id) {
        userStore.remove(id);
        return getAllUsers();
    }

    @Override
    public boolean containsEmail(String email) {
        Boolean contain = !(userStore.values().stream()
                                .filter(usr -> usr.getEmail().equals(email))
                                .collect(Collectors.toList()).isEmpty());
        return contain;
    }

    @Override
    public boolean containsUserId(Integer id) {
        return userStore.containsKey(id);
    }

    private void validate(UserDTO.Database.DBUserDTO userDTO) {
        if (containsEmail(userDTO.getEmail()))
            throw new HttpCustomException(HttpStatus.CONFLICT, this.getClass().toString());
        if (userDTO.getEmail() == null)
            throw new HttpCustomException(HttpStatus.BAD_REQUEST, this.getClass().toString());
    }

    private void validateUpdate(UserDTO.Database.DBUserDTO userDTO) {
        if (!userStore.containsKey(userDTO.getId()))
            throw new HttpCustomException(HttpStatus.NOT_FOUND, this.getClass().toString());
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(userStore.get(userDTO.getId()).getEmail())) {     //email not of this user
            if (containsEmail(userDTO.getEmail()))
                throw new HttpCustomException(HttpStatus.CONFLICT, this.getClass().toString());
        }
    }
}