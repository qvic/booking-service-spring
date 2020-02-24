package com.salon.booking.mapper;

import com.salon.booking.domain.Role;
import com.salon.booking.entity.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper extends AbstractEnumMapper<RoleEntity, Role> {

    public RoleMapper() {
        super(RoleEntity.values(), Role.values());
    }
}
