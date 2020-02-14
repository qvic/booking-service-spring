package com.epam.bookingservice.mapper;

import com.epam.bookingservice.domain.Role;
import com.epam.bookingservice.domain.User;
import com.epam.bookingservice.entity.RoleEntity;
import com.epam.bookingservice.entity.UserEntity;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserMapperTest {

    @Mock
    private Mapper<RoleEntity, Role> roleMapper;

    @InjectMocks
    private UserMapper userMapper;

    @After
    public void resetMocks() {
        reset(roleMapper);
    }

    @Test
    public void mapDomainToEntityShouldMapCorrectly() {
        when(roleMapper.mapDomainToEntity(eq(Role.CLIENT))).thenReturn(RoleEntity.CLIENT);

        User user = User.builder()
                .password("password")
                .email("email")
                .name("name")
                .id(1)
                .role(Role.CLIENT)
                .build();

        UserEntity expectedUserEntity = UserEntity.builder()
                .password("password")
                .email("email")
                .name("name")
                .id(1)
                .role(RoleEntity.CLIENT)
                .build();

        assertEquals(expectedUserEntity, userMapper.mapDomainToEntity(user));
    }

    @Test
    public void mapEntityToDomainShouldMapCorrectly() {
        when(roleMapper.mapEntityToDomain(eq(RoleEntity.CLIENT))).thenReturn(Role.CLIENT);

        UserEntity userEntity = UserEntity.builder()
                .password("password")
                .email("email")
                .name("name")
                .id(1)
                .role(RoleEntity.CLIENT)
                .build();

        User expectedUser = User.builder()
                .password("password")
                .email("email")
                .name("name")
                .id(1)
                .role(Role.CLIENT)
                .build();

        assertEquals(expectedUser, userMapper.mapEntityToDomain(userEntity));
    }

    @Test
    public void mapDomainToEntityShouldReturnNullOnNullParameter() {
        assertNull(userMapper.mapDomainToEntity(null));
    }

    @Test
    public void mapEntityToDomainShouldReturnNullOnNullParameter() {
        assertNull(userMapper.mapEntityToDomain(null));
    }
}