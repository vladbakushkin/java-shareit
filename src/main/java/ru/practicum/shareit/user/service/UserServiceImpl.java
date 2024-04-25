package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.NewUserDtoMapper;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

        checkUserEmailInStorage(user);

        User savedUser = userRepository.save(user);

        return newUserDtoMapper.toDto(savedUser);
    }

    @Override
    public UpdateUserDto updateUser(Long userId, UpdateUserDto updateUserDto) {
        User user = updateUserDtoMapper.toUser(updateUserDto);
        user.setId(userId);

        User userToUpdate = userRepository.findUserById(userId);
        if (userToUpdate == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            checkUserEmailInStorage(user);
            userToUpdate.setEmail(user.getEmail());
        }

        User updatedUser = userRepository.update(userId, userToUpdate);
        return updateUserDtoMapper.toDto(updatedUser);
    }

    @Override
    public NewUserDto getUser(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        return newUserDtoMapper.toDto(user);
    }

    @Override
    public List<NewUserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(newUserDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUserById(id);
    }

    private void checkUserEmailInStorage(User user) {
        Optional<User> foundUser = userRepository.findAll().stream()
                .filter(savedUser -> savedUser.getEmail().equalsIgnoreCase(user.getEmail()))
                .findFirst();

        if (foundUser.isPresent() && !Objects.equals(user.getId(), foundUser.get().getId())) {
            throw new AlreadyExistsException("User already exists");
        }
    }
}
