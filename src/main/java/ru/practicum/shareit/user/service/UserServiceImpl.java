package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        checkUserEmailInStorage(user);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, User user) {
        User userToUpdate = getUser(userId);
        user.setId(userId);
        checkUserEmailInStorage(user);

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }

        return userRepository.update(userId, userToUpdate);
    }

    @Override
    public User getUser(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUserById(id);
    }

    private void checkUserEmailInStorage(User user) {
        Optional<User> foundUser = getAllUsers().stream()
                .filter(savedUser -> savedUser.getEmail().equalsIgnoreCase(user.getEmail()))
                .findFirst();

        if (foundUser.isPresent() && !Objects.equals(user.getId(), foundUser.get().getId())) {
            throw new AlreadyExistsException("User already exists");
        }
    }
}
