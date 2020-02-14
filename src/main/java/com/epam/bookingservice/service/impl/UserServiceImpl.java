package com.epam.bookingservice.service.impl;

import com.epam.bookingservice.domain.Role;
import com.epam.bookingservice.domain.User;
import com.epam.bookingservice.entity.RoleEntity;
import com.epam.bookingservice.entity.UserEntity;
import com.epam.bookingservice.mapper.Mapper;
import com.epam.bookingservice.respository.UserRepository;
import com.epam.bookingservice.service.UserService;
import com.epam.bookingservice.service.exception.UserAlreadyExistsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper<UserEntity, User> userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Mapper<UserEntity, User> userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

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
    public Page<User> findAll(Pageable pageable) {
        Page<UserEntity> page = userRepository.findAll(pageable);

        if (page.getNumber() >= page.getTotalPages()) {
            page = userRepository.findAll(PageRequest.of(page.getTotalPages() - 1, page.getSize()));
        }

        return page.map(userMapper::mapEntityToDomain);
    }

    @Override
    public List<User> findAllWorkers() {
        return userRepository.findAllByRole(RoleEntity.WORKER)
                .stream()
                .map(userMapper::mapEntityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(userMapper::mapEntityToDomain)
                .orElseThrow(() -> new UsernameNotFoundException("User [" + username + "] was not found"));
    }
}
