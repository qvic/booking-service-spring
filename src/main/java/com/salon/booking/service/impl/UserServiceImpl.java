package com.salon.booking.service.impl;

import com.salon.booking.domain.User;
import com.salon.booking.entity.RoleEntity;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.UserRepository;
import com.salon.booking.service.UserService;
import com.salon.booking.service.exception.NoSuchItemException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Mapper<UserEntity, User> userMapper;

    @Override
    public Page<User> findAllWorkers(Pageable properties) {
        return findAllByRole(properties, RoleEntity.WORKER);
    }

    @Override
    public Page<User> findAllClients(Pageable properties) {
        return findAllByRole(properties, RoleEntity.CLIENT);
    }

    private Page<User> findAllByRole(Pageable properties, RoleEntity worker) {
        Page<UserEntity> page = userRepository.findAllByRole(worker, properties);

        return page.map(userMapper::mapEntityToDomain);
    }

    @Override
    @Transactional
    public void promoteToWorker(Integer clientId) {
        UserEntity updatedUser = userRepository.findById(clientId)
                .filter(user -> user.getRole().equals(RoleEntity.CLIENT))
                .map(user -> user.toBuilder()
                        .role(RoleEntity.WORKER)
                        .build())
                .orElseThrow(NoSuchItemException::new);

        userRepository.save(updatedUser);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::mapEntityToDomain);
    }
}
