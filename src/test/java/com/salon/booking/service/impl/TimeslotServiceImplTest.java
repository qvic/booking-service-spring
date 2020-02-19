package com.salon.booking.service.impl;

import com.salon.booking.domain.Order;
import com.salon.booking.domain.Timeslot;
import com.salon.booking.domain.Timetable;
import com.salon.booking.entity.OrderEntity;
import com.salon.booking.entity.ServiceEntity;
import com.salon.booking.entity.TimeslotEntity;
import com.salon.booking.entity.UserEntity;
import com.salon.booking.mapper.Mapper;
import com.salon.booking.repository.ServiceRepository;
import com.salon.booking.repository.TimeslotRepository;
import com.salon.booking.repository.UserRepository;
import com.salon.booking.repository.OrderRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TimeslotServiceImplTest {

    private static final LocalDate FROM_DATE = LocalDate.of(2020, 2, 4);
    private static final LocalDate TO_DATE = LocalDate.of(2020, 2, 9);

    private static final LocalDateTime ORDER_CREATION_DATE = LocalDateTime.of(2020, 1, 1, 12, 0);

    private static final UserEntity USER_ENTITY = UserEntity.builder()
            .id(0)
            .build();

    private static final ServiceEntity SERVICE_ENTITY = ServiceEntity.builder()
            .id(0)
            .build();

    private static final List<OrderEntity> ORDER_ENTITIES = initOrderEntities();

    private static final List<Order> ORDERS = initOrders();

    private static final List<TimeslotEntity> TIMESLOT_ENTITIES = initTimeslotEntities();

    private static final List<Timetable> TIMETABLES = initTimetables();


    @Mock
    private TimeslotRepository timeslotRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mapper<TimeslotEntity, Timeslot> timeslotMapper;

    private TimeslotServiceImpl timeslotService;

    @Before
    public void injectMocks() {
        timeslotService = new TimeslotServiceImpl(timeslotRepository, orderRepository, serviceRepository, userRepository, timeslotMapper);
    }

    @After
    public void resetMocks() {
        reset(timeslotRepository, orderRepository, serviceRepository, userRepository, timeslotMapper);
    }

    @Test
    public void findAllBetweenShouldReturnCorrectTimetables() {
        Timeslot t1 = TIMETABLES.get(1).getRows().get(0);
        Timeslot t2 = TIMETABLES.get(3).getRows().get(0);
        when(timeslotMapper.mapEntityToDomain(eq(TIMESLOT_ENTITIES.get(1)))).thenReturn(t1);
        when(timeslotMapper.mapEntityToDomain(eq(TIMESLOT_ENTITIES.get(2)))).thenReturn(t2);

        when(timeslotRepository.findAllByDateBetween(eq(FROM_DATE), eq(TO_DATE))).thenReturn(TIMESLOT_ENTITIES);

        when(orderRepository.findById(eq(2))).thenReturn(Optional.of(ORDER_ENTITIES.get(1)));
        when(orderRepository.findById(eq(3))).thenReturn(Optional.of(ORDER_ENTITIES.get(2)));

        when(serviceRepository.findById(anyInt())).thenReturn(Optional.empty());

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        List<Timetable> timetables = timeslotService.findAllBetween(FROM_DATE, TO_DATE);

        assertThat(TIMETABLES, equalTo(timetables));
    }

    @Test
    public void updateTimeslotWithOrderShouldSaveOrderAndUpdateTimeslot() {
        Timeslot timeslot = Timeslot.builder()
                .id(123)
                .order(Order.builder()
                        .build())
                .build();
        TimeslotEntity timeslotEntity = TimeslotEntity.builder()
                .id(123)
                .order(OrderEntity.builder()
                        .build())
                .build();
        TimeslotEntity fullTimeslotEntity = TimeslotEntity.builder()
                .id(123)
                .date(LocalDate.of(2, 2, 2))
                .order(OrderEntity.builder()
                        .build())
                .build();
        OrderEntity savedOrder = OrderEntity.builder()
                .id(321)
                .build();
        TimeslotEntity updatedTimeslotEntity = fullTimeslotEntity.toBuilder()
                .order(savedOrder)
                .build();

        when(timeslotMapper.mapDomainToEntity(eq(timeslot))).thenReturn(timeslotEntity);
        when(timeslotRepository.findById(eq(123))).thenReturn(Optional.of(fullTimeslotEntity));
        when(orderRepository.save(eq(timeslotEntity.getOrder()))).thenReturn(savedOrder);

        timeslotService.updateTimeslotWithOrder(timeslot);

        verify(timeslotRepository).save(updatedTimeslotEntity);
    }

    private static List<OrderEntity> initOrderEntities() {
        return Arrays.asList(
                OrderEntity.builder()
                        .id(1)
                        .date(ORDER_CREATION_DATE)
                        .worker(USER_ENTITY)
                        .client(USER_ENTITY)
                        .service(SERVICE_ENTITY)
                        .build(),

                OrderEntity.builder()
                        .id(2)
                        .date(ORDER_CREATION_DATE)
                        .worker(USER_ENTITY)
                        .client(USER_ENTITY)
                        .service(SERVICE_ENTITY)
                        .build(),

                OrderEntity.builder()
                        .id(3)
                        .date(ORDER_CREATION_DATE)
                        .worker(USER_ENTITY)
                        .client(USER_ENTITY)
                        .service(SERVICE_ENTITY)
                        .build());
    }

    private static List<Order> initOrders() {
        return Arrays.asList(
                Order.builder()
                        .date(ORDER_CREATION_DATE)
                        .build(),

                Order.builder()
                        .date(ORDER_CREATION_DATE)
                        .build(),

                Order.builder()
                        .date(ORDER_CREATION_DATE)
                        .build());
    }

    private static List<TimeslotEntity> initTimeslotEntities() {
        return Arrays.asList(
                TimeslotEntity.builder()
                        .id(122)
                        .order(ORDER_ENTITIES.get(0))
                        .fromTime(LocalTime.of(8, 0))
                        .toTime(LocalTime.of(8, 30))
                        .date(LocalDate.of(2020, 2, 2))
                        .build(),
                TimeslotEntity.builder()
                        .id(123)
                        .order(ORDER_ENTITIES.get(1))
                        .fromTime(LocalTime.of(8, 30))
                        .toTime(LocalTime.of(9, 0))
                        .date(LocalDate.of(2020, 2, 5))
                        .build(),
                TimeslotEntity.builder()
                        .id(124)
                        .order(ORDER_ENTITIES.get(2))
                        .fromTime(LocalTime.of(9, 30))
                        .toTime(LocalTime.of(10, 0))
                        .date(LocalDate.of(2020, 2, 7))
                        .build()
        );
    }

    private static List<Timetable> initTimetables() {
        return Arrays.asList(
                new Timetable(LocalDate.of(2020, 2, 4), Collections.emptyList()),
                new Timetable(LocalDate.of(2020, 2, 5),
                        Collections.singletonList(Timeslot.builder()
                                .id(123)
                                .fromTime(LocalTime.of(8, 30))
                                .toTime(LocalTime.of(9, 0))
                                .date(LocalDate.of(2020, 2, 5))
                                .order(ORDERS.get(0))
                                .build())
                ),
                new Timetable(LocalDate.of(2020, 2, 6), Collections.emptyList()),
                new Timetable(LocalDate.of(2020, 2, 7),
                        Collections.singletonList(Timeslot.builder()
                                .id(124)
                                .fromTime(LocalTime.of(9, 30))
                                .toTime(LocalTime.of(10, 0))
                                .date(LocalDate.of(2020, 2, 7))
                                .order(ORDERS.get(0))
                                .build())
                ),
                new Timetable(LocalDate.of(2020, 2, 8), Collections.emptyList()));
    }
}