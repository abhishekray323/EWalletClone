package com.example.User.service;

import com.example.User.entities.User;
import com.example.User.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JPAUserDetailService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    /**
     * Loads user details by username (email).
     *
     * @param email the email of the user
     * @return UserDetails containing user information
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .disabled(false)
                .authorities("ROLE_USER")
                .build();
    }
}
