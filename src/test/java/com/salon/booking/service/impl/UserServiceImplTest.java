package com.salon.booking.service.impl;

import com.salon.booking.domain.Role;
import com.salon.booking.domain.User;
import com.salon.booking.domain.UserLoginForm;
import com.salon.booking.entity.RoleEntity;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.UserRepository;
import com.salon.booking.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String NAME = "name";
    private static final String ENCODED_PASSWORD = "encoded_password";
    private static final String USER_EMAIL = "user@email.com";

    private static final User USER = initUser();
    private static final UserEntity USER_ENTITY = initUserEntity();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Mapper<UserEntity, User> userMapper;

    private UserServiceImpl userService;

    @Before
    public void injectMocks() {
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @After
    public void resetMocks() {
        reset(userRepository, passwordEncoder, userMapper);
    }

    @Test
    public void findAllWorkersShouldReturnMappedWorkers() {
        UserEntity workerEntity = USER_ENTITY.toBuilder().role(RoleEntity.WORKER).build();
        User worker = USER.toBuilder().role(Role.WORKER).build();

        when(userMapper.mapEntityToDomain(eq(workerEntity))).thenReturn(worker);

        Pageable properties = PageRequest.of(0, 1);
        Page<UserEntity> workersPage = new PageImpl<>(Collections.singletonList(workerEntity),
                properties, 1);
        when(userRepository.findAllByRole(eq(RoleEntity.WORKER), eq(properties))).thenReturn(workersPage);

        Page<User> workers = userService.findAllWorkers(properties);

        assertThat(Collections.singletonList(worker), is(workers.getContent()));
    }

    @Test
    public void findAllWorkersShouldReturnLastPageIfPageNumberIsTooBig() {
        Pageable requestedProperties = PageRequest.of(2, 1);
        Page<UserEntity> daoPage = new PageImpl<>(Collections.emptyList(), requestedProperties, 2);
        Page<UserEntity> lastPage = new PageImpl<>(Collections.singletonList(USER_ENTITY), PageRequest.of(1, 1), 2);
        Page<User> mappedLastPage = new PageImpl<>(Collections.singletonList(USER), PageRequest.of(1, 1), 2);

        when(userMapper.mapEntityToDomain(eq(USER_ENTITY))).thenReturn(USER);
        when(userRepository.findAll(eq(requestedProperties))).thenReturn(daoPage);
        when(userRepository.findAll(eq(lastPage.getPageable()))).thenReturn(lastPage);

        Page<User> page = userService.findAllClients(requestedProperties);

        assertArrayEquals(page.getContent().toArray(), mappedLastPage.getContent().toArray());
        assertEquals(page.getPageable(), mappedLastPage.getPageable());
    }

    private static UserEntity initUserEntity() {
        return UserEntity.builder()
                .name(NAME)
                .email(USER_EMAIL)
                .password(ENCODED_PASSWORD)
                .role(RoleEntity.CLIENT)
                .build();
    }

    private static User initUser() {
        return User.builder()
                .name(NAME)
                .email(USER_EMAIL)
                .password(ENCODED_PASSWORD)
                .role(Role.CLIENT)
                .build();
    }
}