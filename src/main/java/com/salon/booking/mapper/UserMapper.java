package com.salon.booking.mapper;

import com.salon.booking.domain.Role;
import com.salon.booking.domain.User;
import com.salon.booking.entity.RoleEntity;
import com.salon.booking.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper implements Mapper<UserEntity, User> {

    private final Mapper<RoleEntity, Role> roleMapper;

    @Override
    public UserEntity mapDomainToEntity(User user) {
        if (user == null) {
            return null;
        }

        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(roleMapper.mapDomainToEntity(user.getRole()))
                .build();
    }

    @Override
    public User mapEntityToDomain(UserEntity user) {
        if (user == null) {
            return null;
        }

        return User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(roleMapper.mapEntityToDomain(user.getRole()))
                .build();
    }
}
