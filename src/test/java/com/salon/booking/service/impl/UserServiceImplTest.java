package com.salon.booking.service.impl;

import com.salon.booking.mapper.Mapper;
import com.salon.booking.domain.User;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final User USER = initUser();
    private static final UserEntity USER_ENTITY = initUserEntity();

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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void findAllShouldReturnLastPageIfPageNumberIsTooBig() {
        Pageable requestedProperties = PageRequest.of(2, 1);
        Page<UserEntity> daoPage = new PageImpl<>(Collections.emptyList(), requestedProperties, 2);
        Page<UserEntity> lastPage = new PageImpl<>(Collections.singletonList(USER_ENTITY), PageRequest.of(1, 1), 2);
        Page<User> mappedLastPage = new PageImpl<>(Collections.singletonList(USER), PageRequest.of(1, 1), 2);

        when(userMapper.mapEntityToDomain(eq(USER_ENTITY))).thenReturn(USER);
        when(userRepository.findAll(eq(requestedProperties))).thenReturn(daoPage);
        when(userRepository.findAll(eq(lastPage.getPageable()))).thenReturn(lastPage);

        Page<User> page = userService.findAll(requestedProperties);

        assertArrayEquals(page.getContent().toArray(), mappedLastPage.getContent().toArray());
        assertEquals(page.getPageable(), mappedLastPage.getPageable());
    }

    private static User initUser() {
        return User.builder()
                .build();
    }

    private static UserEntity initUserEntity() {
        return UserEntity.builder()
                .build();
    }
}