package com.salon.booking.service.impl;

import com.salon.booking.entity.RoleEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.domain.User;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.repository.UserRepository;
import com.salon.booking.service.UserService;
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
