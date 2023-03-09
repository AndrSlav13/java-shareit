package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exceptions.HttpCustomException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void getSimpleUser() {
        Mockito
                .when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(user));
        User it = userService.getSimpleUser(userId);

        assertEquals(userId, it.getId());

        Mockito
                .when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        HttpCustomException ex = assertThrows(HttpCustomException.class,
                () -> userService.getSimpleUser(userId));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getCode());
    }

    @Test
    void getUserById() {
        Mockito
                .when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(user));
        UserDTO.Controller.ReturnUserDTO it = userService.getUserById(userId);

        assertEquals(userId, it.getId());

        Mockito
                .when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.empty());

        HttpCustomException ex = assertThrows(HttpCustomException.class,
                () -> userService.getUserById(userId));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getCode());
    }

    @Test
    void getAllUsers() {
        Mockito
                .when(userRepository.findAllByIdNotNull())
                .thenReturn(List.of(user));
        List<UserDTO.Controller.ReturnUserDTO> it = userService.getAllUsers();

        assertEquals(userId, it.get(0).getId());
    }

    @Test
    void addUser() {
        user = user.toBuilder().id(null).build();
        Mockito
                .when(userRepository.save(user))
                .thenReturn(user);
        UserDTO.Controller.ReturnUserDTO it = userService.addUser(user);

        assertEquals(userEmail, it.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void updateUserById() {
        Mockito
                .when(userRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(user));
        UserDTO.Controller.ReturnUserDTO it = userService.updateUserById(user2, userId);

        assertEquals(userId, it.getId());
        assertEquals(userName2, it.getName());
        assertEquals(userEmail2, it.getEmail());
    }

    @Test
    void deleteUserById() {
        Mockito
                .doNothing().when(userRepository).deleteById(Mockito.any(Long.class));
        userService.deleteUserById(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    void isUserBooker() {
        Mockito
                .doNothing().when(userRepository).deleteById(Mockito.any(Long.class));
        userService.deleteUserById(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    void isEarlierItemBookedByUser() {
        Mockito
                .when(userRepository.isEarlierItemBookedByUser(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(true);
        userService.isEarlierItemBookedByUser(itemId, userId);

        Mockito.verify(userRepository, Mockito.times(1)).isEarlierItemBookedByUser(itemId, userId);
    }

    @Test
    void isUserOwner() {
        Mockito
                .when(userRepository.isUserOwner(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(true);
        userService.isUserOwner(itemId, userId);

        Mockito.verify(userRepository, Mockito.times(1)).isUserOwner(itemId, userId);
    }

    @Test
    void isUserOwnerOrBooker() {
        Mockito
                .when(userRepository.isUserOwnerOrBooker(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(true);
        userService.isUserOwnerOrBooker(itemId, userId);

        Mockito.verify(userRepository, Mockito.times(1)).isUserOwnerOrBooker(itemId, userId);
    }

    @Test
    void isUserAbleToBook() {
        Mockito
                .when(userRepository.isUserAbleToBook(Mockito.any(Long.class), Mockito.any(Long.class)))
                .thenReturn(true);
        userService.isUserAbleToBook(itemId, userId);

        Mockito.verify(userRepository, Mockito.times(1)).isUserAbleToBook(itemId, userId);
    }


    String userEmail = "qwerty@yandex.ru";
    String userName = "John";
    String itemName = "Щетка";
    String itemDescription = "something";
    Boolean itemAvailable = true;
    String userEmail2 = "qwerty@google.ru";
    String userName2 = "Sam";
    Long userId = 2L;
    Long userId2 = 11L;
    Long itemId = 25L;

    User user = null;
    User user2 = null;
    Item item = null;

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
        item = Item.builder()
                .id(itemId)
                .requests(new ArrayList<>())
                .bookings(new ArrayList<>())
                .comments(new ArrayList<>())
                .owner(user)
                .description(itemDescription)
                .name(itemName)
                .available(itemAvailable)
                .build();
    }

}