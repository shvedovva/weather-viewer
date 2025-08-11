package com.example.weatherviewer.service;

import com.example.weatherviewer.entity.User;
import com.example.weatherviewer.exception.UserAlreadyExistsException;
import com.example.weatherviewer.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String login, String password) throws UserAlreadyExistsException {
        if (userRepository.existsByLogin(login)) {
            throw new UserAlreadyExistsException("Пользователь с таким логином уже существует.");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(login, encodedPassword);
        return userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Integer id){
        return userRepository.findById(id);
    }
    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login){
        return userRepository.findByLogin(login);
    }

    public boolean checkPassword(User user, String password){
        return passwordEncoder.matches(password, user.getPassword());
    }

}
