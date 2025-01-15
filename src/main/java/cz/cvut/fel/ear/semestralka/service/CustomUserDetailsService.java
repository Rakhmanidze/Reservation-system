package cz.cvut.fel.ear.semestralka.service;

import cz.cvut.fel.ear.semestralka.dao.UserDao;
import cz.cvut.fel.ear.semestralka.exception.UserNotFoundException;
import cz.cvut.fel.ear.semestralka.model.Admin;
import cz.cvut.fel.ear.semestralka.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
        User user = userDao.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        String role = (user instanceof Admin) ?  "ROLE_ADMIN" : "ROLE_USER";

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .authorities(role)
            .build();
    }
}