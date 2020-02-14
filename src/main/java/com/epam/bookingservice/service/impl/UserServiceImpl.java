package com.epam.bookingservice.service.impl;

import com.epam.bookingservice.domain.User;
import com.epam.bookingservice.entity.RoleEntity;
import com.epam.bookingservice.entity.UserEntity;
import com.epam.bookingservice.mapper.Mapper;
import com.epam.bookingservice.repository.UserRepository;
import com.epam.bookingservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Mapper<UserEntity, User> userMapper;

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
}
