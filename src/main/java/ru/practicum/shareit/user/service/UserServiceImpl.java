package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.NewUserDtoMapper;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final NewUserDtoMapper newUserDtoMapper = new NewUserDtoMapper();
    private final UpdateUserDtoMapper updateUserDtoMapper = new UpdateUserDtoMapper();

    @Override
    public NewUserDto saveUser(NewUserDto newUserDto) {
        User user = newUserDtoMapper.toUser(newUserDto);
        User savedUser = userRepository.save(user);
        return newUserDtoMapper.toDto(savedUser);
    }

    @Override
    public UpdateUserDto updateUser(Long userId, UpdateUserDto updateUserDto) {
        User user = updateUserDtoMapper.toUser(updateUserDto);

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }

        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }

        User updatedUser = userRepository.save(userToUpdate);
        return updateUserDtoMapper.toDto(updatedUser);
    }

    @Override
    public NewUserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        return newUserDtoMapper.toDto(user);
    }

    @Override
    public List<NewUserDto> getAllUsers() {
        // возможно стоит добавить пагинацию
        return userRepository.findAll().stream()
                .map(newUserDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
