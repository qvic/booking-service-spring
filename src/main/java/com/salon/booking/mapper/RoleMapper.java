package com.salon.booking.mapper;

import com.salon.booking.entity.RoleEntity;
import com.salon.booking.domain.Role;
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
