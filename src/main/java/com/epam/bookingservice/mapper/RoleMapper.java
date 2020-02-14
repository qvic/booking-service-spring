package com.epam.bookingservice.mapper;

import com.epam.bookingservice.domain.Role;
import com.epam.bookingservice.entity.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper implements Mapper<RoleEntity, Role> {

    @Override
    public RoleEntity mapDomainToEntity(Role role) {
        if (role == null) {
            return null;
        }

        return RoleEntity.findByName(role.name()).orElse(null);
    }

    @Override
    public Role mapEntityToDomain(RoleEntity role) {
        if (role == null) {
            return null;
        }

        return Role.findByName(role.name()).orElse(null);
    }
}
