package com.salon.booking.service.impl;

import com.salon.booking.service.exception.UserAlreadyExistsException;
import com.salon.booking.domain.Role;
import com.salon.booking.domain.User;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.UserRepository;
import com.salon.booking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper<UserEntity, User> userMapper;

    @Override
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User encodedUser = preparePasswordAndRole(user);
        UserEntity savedEntity = userRepository.save(userMapper.mapDomainToEntity(encodedUser));
        return userMapper.mapEntityToDomain(savedEntity);
    }

    private User preparePasswordAndRole(User user) {
        return user.toBuilder()
                .role(Role.CLIENT)
                .password(passwordEncoder.encode(user.getPassword()))
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(userMapper::mapEntityToDomain)
                .orElseThrow(() -> new UsernameNotFoundException("User [" + username + "] was not found"));
    }
}
