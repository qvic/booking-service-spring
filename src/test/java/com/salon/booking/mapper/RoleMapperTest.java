package com.salon.booking.mapper;

import com.salon.booking.entity.RoleEntity;
import com.salon.booking.domain.Role;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class RoleMapperTest {

    private RoleMapper roleMapper;

    @Before
    public void before() {
        roleMapper = new RoleMapper();
    }

    @Test
    public void mapDomainToEntityShouldReturnCorrectEnumValue() {
        TestCase.assertEquals(RoleEntity.CLIENT, roleMapper.mapDomainToEntity(Role.CLIENT));
    }

    @Test
    public void mapEntityToDomainShouldReturnCorrectEnumValue() {
        assertEquals(Role.CLIENT, roleMapper.mapEntityToDomain(RoleEntity.CLIENT));
    }

    @Test
    public void mapDomainToEntityShouldReturnNullOnNullParameter() {
        TestCase.assertNull(roleMapper.mapDomainToEntity(null));
    }

    @Test
    public void mapEntityToDomainShouldReturnNullOnNullParameter() {
        assertNull(roleMapper.mapEntityToDomain(null));
    }
}