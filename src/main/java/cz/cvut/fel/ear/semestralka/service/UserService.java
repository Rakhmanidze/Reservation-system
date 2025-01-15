package cz.cvut.fel.ear.semestralka.service;

import cz.cvut.fel.ear.semestralka.dao.UserDao;
import cz.cvut.fel.ear.semestralka.dto.UserDto;
import cz.cvut.fel.ear.semestralka.exception.UserNotFoundException;
import cz.cvut.fel.ear.semestralka.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableWebSecurity
public class UserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public List<UserDto> getAllUsers() {
        List<User> users = userDao.findAll();
        return users.stream().map(this::userToDto).collect(Collectors.toList());
    }

    @Transactional
    public UserDto getUserById(Integer id) {
        User user = userDao.find(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }
        return userToDto(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        return userToDto(user);
    }

    @Transactional
    public UserDto updateUser(UserDto userDto) {
        User existingUser = userDao.find(userDto.getId());
        if (existingUser == null) {
            throw new UserNotFoundException("User not found with ID: " + userDto.getId());
        }

        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setRoomNumber(userDto.getRoomNumber());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(userDto.getPassword());
            existingUser.setPassword(hashedPassword);
        }

        userDao.update(existingUser);
        return userToDto(existingUser);
    }


    @Transactional
    public void validateUserForRegistration(User user) {
        Objects.requireNonNull(user, "User must not be null");
        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new IllegalArgumentException("Email is required.");
        if (user.getFirstName() == null || user.getFirstName().isEmpty())
            throw new IllegalArgumentException("First name is required.");
        if (user.getLastName() == null || user.getLastName().isEmpty())
            throw new IllegalArgumentException("Last name is required.");
        if (user.getPassword() == null || user.getPassword().isEmpty())
            throw new IllegalArgumentException("Password is required.");
        if (userDao.existsByEmail(user.getEmail()))
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists.");
    }

    private UserDto userToDto(User user) {
        return UserDto.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roomNumber(user.getRoomNumber())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public User userToEntity(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .roomNumber(userDto.getRoomNumber())
                .phoneNumber(userDto.getPhoneNumber())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build();
    }
}
