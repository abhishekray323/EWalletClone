package com.example.User.service;

import com.example.User.customExceptions.UserAlreadyExistsException;
import com.example.User.entities.User;
import com.example.User.entities.models.WalletCreationEvent;
import com.example.User.entities.request.UserRequestDTO;
import com.example.User.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private KafkaTemplate<String, WalletCreationEvent> kafkaTemplate;

    /**
     * Registers a new user.
     * @param userRequestDTO the user request data transfer object containing user details.
     * @return the registered User entity.
     * @throws UserAlreadyExistsException if a user with the same email already exists.
     */
    public User register(UserRequestDTO userRequestDTO) {
        User user = User.builder()
                .name(userRequestDTO.getName())
                .email(userRequestDTO.getEmail())
                .phoneNumber(userRequestDTO.getPhoneNumber())
                .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                .build();

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(user.getName(), user.getEmail());
        }
        user = userRepository.save(user);
        WalletCreationEvent walletCreationEvent = WalletCreationEvent.builder()
                                                                .userId(user.getId())
                                                                .phoneNumber(user.getPhoneNumber())
                                                                .build();
        //kafkaTemplate.send("wallet-creation-topic", walletCreationEvent);
        return user;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
