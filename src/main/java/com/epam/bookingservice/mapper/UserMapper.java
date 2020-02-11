package com.epam.bookingservice.mapper;

import com.epam.bookingservice.domain.Role;
import com.epam.bookingservice.domain.User;
import com.epam.bookingservice.entity.RoleEntity;
import com.epam.bookingservice.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<UserEntity, User> {

    private final Mapper<RoleEntity, Role> roleMapper;

    public UserMapper(Mapper<RoleEntity, Role> roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public UserEntity mapDomainToEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return UserEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .role(roleMapper.mapDomainToEntity(domain.getRole()))
                .build();
    }

    @Override
    public User mapEntityToDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .role(roleMapper.mapEntityToDomain(entity.getRole()))
                .build();
    }
}
