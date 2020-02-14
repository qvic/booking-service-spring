package com.epam.bookingservice.service.impl;

import com.epam.bookingservice.domain.Order;
import com.epam.bookingservice.domain.Timeslot;
import com.epam.bookingservice.domain.Timetable;
import com.epam.bookingservice.entity.OrderEntity;
import com.epam.bookingservice.entity.ServiceEntity;
import com.epam.bookingservice.entity.TimeslotEntity;
import com.epam.bookingservice.entity.UserEntity;
import com.epam.bookingservice.mapper.Mapper;
import com.epam.bookingservice.repository.OrderRepository;
import com.epam.bookingservice.repository.ServiceRepository;
import com.epam.bookingservice.repository.TimeslotRepository;
import com.epam.bookingservice.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
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
    public void findAllBetween() {
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