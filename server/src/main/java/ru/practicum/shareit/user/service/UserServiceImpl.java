package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.UserDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        User user = UserDtoMapper.toUser(userRequestDto);
        User savedUser = userRepository.save(user);
        return UserDtoMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto) {
        User user = UserDtoMapper.toUser(userRequestDto);

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }

        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }

        User updatedUser = userRepository.save(userToUpdate);
        return UserDtoMapper.toDto(updatedUser);
    }

    @Override
    public UserResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        return UserDtoMapper.toDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers(Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).stream()
                .map(UserDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
