package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    User savedUser;
    User updatedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User(1L, "user@email.com", "User");
        updatedUser = new User(1L, "user@email.com", "New Name");
    }

    @Test
    void saveUser_Valid_ReturnsUser() {
        // given
        UserNewDto userToSave = createNewUserDto();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        UserResponseDto savedUser = userService.saveUser(userToSave);

        // then
        assertNotNull(savedUser);
        assertEquals(userToSave.getName(), savedUser.getName());
        assertEquals(userToSave.getEmail(), savedUser.getEmail());
    }

    @Test
    void updateUser_Valid_ReturnsUser() {
        // given
        UserUpdateDto userToUpdate = createUpdateUserDto();
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(updatedUser));

        // when
        UserResponseDto updatedUser = userService.updateUser(userToUpdate.getId(), userToUpdate);

        // then
        assertNotNull(updatedUser);
        assertEquals(userToUpdate.getName(), updatedUser.getName());
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());
    }

    @Test
    void getUser_ReturnsUser() {
        // given
        UserNewDto userToGet = createNewUserDto();
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(savedUser));

        // when
        UserResponseDto user = userService.getUser(1L);

        // then
        assertNotNull(user);
        assertEquals(userToGet.getName(), user.getName());
        assertEquals(userToGet.getEmail(), user.getEmail());
    }

    @Test
    void getAllUsers_ReturnsAllUsers() {
        // given
        UserNewDto newUserToGet = createNewUserDto();
        UserUpdateDto updateUserToGet = createUpdateUserDto();
        when(userRepository.findAll()).thenReturn(List.of(savedUser, updatedUser));

        // when
        List<UserResponseDto> users = userService.getAllUsers();

        // then
        assertNotNull(users);

        assertEquals(newUserToGet.getName(), users.get(0).getName());
        assertEquals(newUserToGet.getEmail(), users.get(0).getEmail());

        assertEquals(updateUserToGet.getName(), users.get(1).getName());
        assertEquals(updateUserToGet.getEmail(), users.get(1).getEmail());
    }

    @Test
    void deleteUser() {
        // given
        doNothing().when(userRepository).deleteById(any(Long.class));

        // when
        userService.deleteUser(1L);

        // then
        verify(userRepository, times(1)).deleteById(any(Long.class));
    }

    private UserNewDto createNewUserDto() {
        UserNewDto userNewDto = new UserNewDto();
        userNewDto.setName("User");
        userNewDto.setEmail("user@email.com");
        return userNewDto;
    }

    private UserUpdateDto createUpdateUserDto() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(1L);
        userUpdateDto.setName("New Name");
        userUpdateDto.setEmail("user@email.com");
        return userUpdateDto;
    }
}