package com.crud.demo.service;

import com.crud.demo.dto.UserDTO;
import com.crud.demo.entity.User;
import com.crud.demo.exception.UserException;
import com.crud.demo.payload.request.SignUpRequest;
import com.crud.demo.repository.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;

@Service
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RegistrationEmailService mailService;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RegistrationEmailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public User createUser(SignUpRequest userIn) {
        User user = new User();
        user.setEmail(userIn.getEmail());
        user.setUsername(userIn.getUsername());
        user.setFirstName(userIn.getFirstName());
        user.setLastName(userIn.getLastName());
        user.setPassword(passwordEncoder.encode(userIn.getPassword()));
        user.setEnabled(false);
        user.setVerificationCode(RandomStringUtils.randomAlphanumeric(32));
        mailService.sendVerificationCode(user);
        try {
            LOG.info("Saving User {}", userIn.getEmail());
            return userRepository.save(user);
        } catch (Exception e) {
            LOG.error("Error during registration. {}", e.getMessage());
            throw new UserException("The user " + user.getUsername() + " cannot be saved Please check credentials");
        }
    }

    public User updateUser(UserDTO userDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBio(userDTO.getBio());

        return userRepository.save(user);
    }

    public boolean deleteUser(Long userId, Principal principal) {
        LOG.info("userId"+userId);
        User user = null;
        try{
            user = getUserById(userId);
        } catch (UserException e) {
            LOG.error(e.getMessage());
        }


        if(user.getUsername().equals(principal.getName())) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public User getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UserException("Username not found with username " + username));

    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserException("User not found"));
    }

    public void verifyUser(String code) {
        final Optional<User> optionalUser = userRepository.findByVerificationCode(code);
        final User user = optionalUser
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user for code: " + code));
        user.setEnabled(true);
        user.setVerificationCode(null);
        userRepository.flush();
        LOG.info("User {} has been activated", user.getEmail());
    }
}

