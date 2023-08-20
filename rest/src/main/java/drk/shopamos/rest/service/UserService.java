package drk.shopamos.rest.service;

import drk.shopamos.rest.model.entity.User;
import drk.shopamos.rest.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public User loadUserByUsername(String username) {
        return userRepository
                .findByEmail(username)
                .orElseThrow(
                        () ->
                                new UsernameNotFoundException(
                                        String.format("%s not found", username)));
    }
}
