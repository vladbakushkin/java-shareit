package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private final UserService userService;
    @Autowired
    private final UserRepository userRepository;

    @Test
    void saveUser() {
        // given
        UserNewDto userNewDto = new UserNewDto();
        userNewDto.setName("Test User");
        userNewDto.setEmail("testuser@example.com");

        // when
        UserResponseDto savedUser = userService.saveUser(userNewDto);

        // then
        assertNotNull(savedUser.getId());
        assertEquals(userNewDto.getName(), savedUser.getName());
        assertEquals(userNewDto.getEmail(), savedUser.getEmail());

        Optional<User> userFromDb = userRepository.findById(savedUser.getId());
        assertTrue(userFromDb.isPresent());
        assertEquals(userNewDto.getName(), userFromDb.get().getName());
        assertEquals(userNewDto.getEmail(), userFromDb.get().getEmail());
    }
}
