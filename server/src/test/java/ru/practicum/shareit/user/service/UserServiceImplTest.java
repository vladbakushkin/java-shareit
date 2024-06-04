package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
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

    private User savedUser;
    private User updatedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User(1L, "user@email.com", "User");
        updatedUser = new User(1L, "user@email.com", "New Name");
    }

    @Test
    void saveUser_Valid_ReturnsUser() {
        // given
        UserRequestDto userToSave = createNewUser1Dto();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        UserResponseDto savedUser = userService.saveUser(userToSave);

        // then
        assertNotNull(savedUser);
        assertEquals(userToSave.getName(), savedUser.getName());
        assertEquals(userToSave.getEmail(), savedUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_Valid_ReturnsUser() {
        // given
        UserRequestDto userToUpdate = createNewUser1Dto();
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(updatedUser));

        // when
        UserResponseDto updatedUser = userService.updateUser(1L, userToUpdate);

        // then
        assertNotNull(updatedUser);
        assertEquals(userToUpdate.getName(), updatedUser.getName());
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WithoutNameValid_ReturnsUser() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");
        UserRequestDto userToUpdate = createNewUser1Dto();
        userToUpdate.setName(null);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(updatedUser));

        // when
        UserResponseDto updatedUser = userService.updateUser(1L, userToUpdate);

        // then
        assertNotNull(updatedUser);
        assertEquals(userToUpdate.getName(), updatedUser.getName());
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WithoutEmailValid_ReturnsUser() {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("New Name");
        UserRequestDto userToUpdate = createNewUser2Dto();
        userToUpdate.setEmail(null);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(updatedUser));

        // when
        UserResponseDto updatedUser = userService.updateUser(1L, userToUpdate);

        // then
        assertNotNull(updatedUser);
        assertEquals(userToUpdate.getName(), updatedUser.getName());
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUser_ReturnsUser() {
        // given
        UserRequestDto userToGet = createNewUser1Dto();
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
        UserRequestDto newUserToGet = createNewUser1Dto();
        UserRequestDto updateUserToGet = createNewUser2Dto();
        Page<User> userPage = new PageImpl<>(List.of(savedUser, updatedUser));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // when
        List<UserResponseDto> users = userService.getAllUsers(0, 10);

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

    private UserRequestDto createNewUser1Dto() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("User");
        userRequestDto.setEmail("user@email.com");
        return userRequestDto;
    }

    private UserRequestDto createNewUser2Dto() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("New Name");
        userRequestDto.setEmail("user@email.com");
        return userRequestDto;
    }
}