package com.epam.bookingservice.service.impl;

import com.epam.bookingservice.domain.Order;
import com.epam.bookingservice.domain.Role;
import com.epam.bookingservice.domain.Service;
import com.epam.bookingservice.domain.User;
import com.epam.bookingservice.entity.OrderEntity;
import com.epam.bookingservice.entity.RoleEntity;
import com.epam.bookingservice.entity.ServiceEntity;
import com.epam.bookingservice.entity.UserEntity;
import com.epam.bookingservice.mapper.Mapper;
import com.epam.bookingservice.respository.OrderRepository;
import com.epam.bookingservice.respository.ServiceRepository;
import com.epam.bookingservice.respository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapper<OrderEntity, Order> orderMapper;

    @Mock
    private Mapper<ServiceEntity, Service> serviceMapper;

    private OrderServiceImpl orderService;

    @Before
    public void injectMocks() {
        orderService = new OrderServiceImpl(orderRepository, serviceRepository, userRepository, orderMapper, serviceMapper);
    }

    @After
    public void resetMocks() {
        reset(orderRepository, serviceRepository, userRepository, orderMapper, serviceMapper);
    }

    @Test
    public void findAllByClientIdShouldReturnMappedUsers() {
        User client = User.builder().id(1).role(Role.CLIENT).build();
        UserEntity clientEntity = UserEntity.builder().id(1).role(RoleEntity.CLIENT).build();

        User worker = User.builder().id(2).role(Role.WORKER).build();
        UserEntity workerEntity = UserEntity.builder().id(2).role(RoleEntity.WORKER).build();

        Service service = Service.builder().id(3).build();
        ServiceEntity serviceEntity = ServiceEntity.builder().id(3).build();

        Order order = Order.builder()
                .service(service)
                .worker(worker)
                .client(client)
                .build();

        OrderEntity orderEntity = OrderEntity.builder()
                .service(serviceEntity)
                .worker(workerEntity)
                .client(clientEntity)
                .build();

        when(orderRepository.findAllByClientId(anyInt())).thenReturn(Collections.singletonList(orderEntity));
        when(orderMapper.mapEntityToDomain(eq(orderEntity))).thenReturn(order);
        when(serviceRepository.findById(anyInt())).thenReturn(Optional.of(serviceEntity));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(workerEntity));

        List<Order> orders = orderService.findAllByClientId(123);

        assertEquals(Collections.singletonList(order), orders);
    }

}