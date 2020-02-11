package com.epam.bookingservice.mapper;

import com.epam.bookingservice.domain.Role;
import com.epam.bookingservice.entity.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper implements Mapper<RoleEntity, Role> {

    @Override
    public RoleEntity mapDomainToEntity(Role domain) {
        if (domain == null) {
            return null;
        }

        return RoleEntity.findByName(domain.name()).orElse(null);
    }

    @Override
    public Role mapEntityToDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }

        return Role.findByName(entity.name()).orElse(null);
    }
}
