package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private final UserService userService;
    @Autowired
    private final UserRepository userRepository;

    @Test
    void saveUser() {
        // given
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("Test User");
        userRequestDto.setEmail("testuser@example.com");

        // when
        UserResponseDto savedUser = userService.saveUser(userRequestDto);

        // then
        assertNotNull(savedUser.getId());
        assertEquals(userRequestDto.getName(), savedUser.getName());
        assertEquals(userRequestDto.getEmail(), savedUser.getEmail());

        Optional<User> userFromDb = userRepository.findById(savedUser.getId());
        assertTrue(userFromDb.isPresent());
        assertEquals(userRequestDto.getName(), userFromDb.get().getName());
        assertEquals(userRequestDto.getEmail(), userFromDb.get().getEmail());
    }
}
