package com.salon.booking.service.impl;

import com.salon.booking.domain.Order;
import com.salon.booking.domain.Role;
import com.salon.booking.domain.SalonService;
import com.salon.booking.domain.Timeslot;
import com.salon.booking.domain.User;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.entity.RoleEntity;
import com.salon.booking.entity.ServiceEntity;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.OrderRepository;
import com.salon.booking.repository.ServiceRepository;
import com.salon.booking.repository.UserRepository;
import com.salon.booking.service.TimeslotService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
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
    private TimeslotService timeslotService;

    @Mock
    private Mapper<OrderEntity, Order> orderMapper;

    @Mock
    private Mapper<ServiceEntity, SalonService> serviceMapper;

    private OrderServiceImpl orderService;

    @Before
    public void injectMocks() {
        orderService = new OrderServiceImpl(timeslotService, orderRepository, serviceRepository, userRepository, orderMapper, serviceMapper);
    }

    @After
    public void resetMocks() {
        reset(orderRepository, serviceRepository, userRepository, orderMapper, serviceMapper);
    }

    @Test
    public void findAllByClientIdShouldReturnMappedOrders() {
        User client = User.builder()
                .id(1)
                .role(Role.CLIENT)
                .build();
        UserEntity clientEntity = UserEntity.builder()
                .id(1)
                .role(RoleEntity.CLIENT)
                .build();

        User worker = User.builder().id(2).role(Role.WORKER).build();
        UserEntity workerEntity = UserEntity.builder()
                .id(2)
                .role(RoleEntity.WORKER)
                .build();

        SalonService service = SalonService.builder()
                .id(3)
                .build();
        ServiceEntity serviceEntity = ServiceEntity.builder()
                .id(3)
                .build();

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

        when(orderRepository.findAllByClientId(anyInt(), any())).thenReturn(new PageImpl<>(Collections.singletonList(orderEntity)));
        when(orderMapper.mapEntityToDomain(eq(orderEntity))).thenReturn(order);
        when(serviceRepository.findById(eq(3))).thenReturn(Optional.of(serviceEntity));
        when(userRepository.findById(eq(1))).thenReturn(Optional.of(clientEntity));
        when(userRepository.findById(eq(2))).thenReturn(Optional.of(workerEntity));

        Page<Order> orders = orderService.findAllByClientId(0, null);

        assertThat(orders.getContent(), equalTo(Collections.singletonList(order)));
    }

    @Test
    public void findAllByWorkerIdShouldReturnMappedOrders() {
        Order order = Order.builder()
                .id(1)
                .build();

        OrderEntity orderEntity = OrderEntity.builder()
                .id(1)
                .build();

        when(orderRepository.findAllByWorkerId(anyInt(), any())).thenReturn(new PageImpl<>(Collections.singletonList(orderEntity)));
        when(orderMapper.mapEntityToDomain(eq(orderEntity))).thenReturn(order);

        Page<Order> orders = orderService.findAllByWorkerId(0, null);

        assertThat(orders.getContent(), equalTo(Collections.singletonList(order)));
    }

    @Test
    public void saveOrderUpdatingTimeslots() {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(1)
                .build();

        when(orderRepository.save(any())).thenReturn(orderEntity);
        when(timeslotService.findTimeslotsForOrderWith(anyInt(), any(), any())).thenReturn(Collections.singletonList(Timeslot.builder().id(2).build()));

        orderService.saveOrderUpdatingTimeslots(1, Order.builder().build());

        verify(timeslotService).saveOrderTimeslot(eq(2), eq(1));
    }
}