package com.salon.booking.mapper;

import com.salon.booking.entity.RoleEntity;
import com.salon.booking.domain.Role;
import com.salon.booking.domain.User;
import com.salon.booking.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper implements Mapper<UserEntity, User> {

    private final Mapper<RoleEntity, Role> roleMapper;

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
